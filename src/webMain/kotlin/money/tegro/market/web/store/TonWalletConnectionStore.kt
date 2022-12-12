package money.tegro.market.web.store

import dev.fritz2.core.SimpleHandler
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.jsObject
import money.tegro.market.web.store.tonwallet.TonWalletProvider
import money.tegro.market.web.store.tonwallet.Wallet
import org.w3c.dom.get

class TonWalletConnectionStore : ConnectionStore<Wallet>() {
    override val address: Flow<String?> = data.map { it?.address }

    override val isAvailable: Flow<Boolean> = data.map { isAvailable() }

    override val isConnected: Flow<Boolean> =
        data.map { wallet -> requestWallets().any { it.address == wallet?.address && it.publicKey == wallet.publicKey } }

    override val connect: SimpleHandler<Unit> = handle { _ ->
        requestWallets().firstOrNull()?.also {
            console.log(it)
        }
    }

    override val disconnect: SimpleHandler<Unit> = handle { _ -> null }

    override val requestTransaction: SimpleHandler<TransactionRequestModel> = handle { wallet, request ->
        sendTransaction(jsObject {
            to = request.dest
            value = request.value.toString()
            this.data = (request.payload ?: request.text)
            dataType = if (request.payload != null) "boc" else "text"
            stateInit = request.stateInit
        })
        wallet
    }

    companion object {
        fun ton() = window.get("ton").unsafeCast<TonWalletProvider?>()
        suspend fun isAvailable(): Boolean = ton() != null

        suspend fun requestWallets() =
            ton()?.send("ton_requestWallets", arrayOf())
                ?.await()
                ?.unsafeCast<Array<Wallet>>()
                .orEmpty()

        suspend fun sendTransaction(request: dynamic): Boolean {
            return ton()?.send("ton_sendTransaction", arrayOf(request))?.await()
                ?.unsafeCast<Boolean>() ?: false
        }
    }
}
