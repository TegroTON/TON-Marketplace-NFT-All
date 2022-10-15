package money.tegro.market.web.model

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import money.tegro.market.web.wallet.TonWalletProvider
import org.w3c.dom.get

@Serializable
data class Connection(
    val id: String = "connection",
    val provider: String? = null,
    val wallet: String? = null,
    val publicKey: String? = null,
) {
    fun isConnected() = provider != null && wallet != null

    companion object {
        fun tonWallet() = window.get("ton").unsafeCast<TonWalletProvider?>()

        suspend fun connectTonWallet() = tonWallet()?.send("ton_requestWallets", arrayOf())?.await()
            ?.unsafeCast<Array<Wallet>>()
            ?.firstOrNull()
            ?.let {
                Connection(
                    provider = "tonwallet",
                    wallet = it.address,
                    publicKey = it.publicKey,
                )
            }
    }
}
