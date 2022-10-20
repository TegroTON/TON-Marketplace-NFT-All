package money.tegro.market.web.model

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.jsObject
import money.tegro.market.web.wallet.TonWalletProvider
import org.w3c.dom.get

@Serializable
data class TonWalletConnection(
    override val walletAddress: String,
    override val publicKey: String,
) : Connection {
    override suspend fun isConnected(): Boolean =
        requestWallets().any { it.address == walletAddress && it.publicKey == publicKey }

    override suspend fun requestTransaction(request: TransactionRequestModel): Boolean =
        sendTransaction(jsObject {
            to = request.dest
            value = request.value.toString()
            data = (request.payload ?: request.text)
            dataType = if (request.payload != null) "boc" else "text"
            stateInit = request.stateInit
        })

    companion object {
        fun isAvailable() = ton() != null

        fun ton() = window.get("ton").unsafeCast<TonWalletProvider?>()

        suspend fun requestWallets() =
            ton()?.send("ton_requestWallets", arrayOf())
                ?.await()
                ?.unsafeCast<Array<Wallet>>()
                .orEmpty()

        suspend fun sendTransaction(request: dynamic): Boolean {
            return ton()?.send("ton_sendTransaction", arrayOf(request))?.await()?.unsafeCast<Boolean>() ?: false
        }

        suspend fun connect() =
            requestWallets().firstOrNull()?.let {
                console.log(it)
                TonWalletConnection(it.address, it.publicKey)
            }
    }
}
