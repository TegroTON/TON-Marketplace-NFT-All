package money.tegro.market.web.store

import dev.fritz2.core.SimpleHandler
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.jsObject
import money.tegro.market.web.store.tonwallet.TonWalletProvider
import money.tegro.market.web.store.tonwallet.Wallet
import org.kodein.di.instance
import org.w3c.dom.get

class TonWalletConnectionStore : ConnectionStore<Wallet>() {
    private val popOverStore: PopOverStore by di.instance()

    override val address: Flow<String?> = data.map { it?.address }

    override val isAvailable: Flow<Boolean> = data.map { isAvailable() }

    override val isConnected: Flow<Boolean> =
        data.map { wallet -> requestWallets().any { it.address == wallet?.address && it.publicKey == wallet.publicKey } }

    override val connect: SimpleHandler<Unit> = handle { _ ->
        requestWallets().firstOrNull()?.also {
            console.log(it)
            localStorage.setItem("ton_wallet", Json.encodeToString(SerializableWallet(it)))
            popOverStore.close()
        }
    }

    override val disconnect: SimpleHandler<Unit> = handle { _ ->
        localStorage.removeItem("ton_wallet")
        null
    }

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

    init {
        localStorage.getItem("ton_wallet")?.let {
            this.update(Json.decodeFromString<SerializableWallet>(it))
        }
    }

    companion object {
        private fun ton() = window.get("ton").unsafeCast<TonWalletProvider?>()
        fun isAvailable(): Boolean = ton() != null

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

@Serializable
private class SerializableWallet(
    override val address: String,
    override val publicKey: String,
    override val walletVersion: String
) : Wallet {
    constructor(wallet: Wallet) : this(wallet.address, wallet.publicKey, wallet.walletVersion)
}
