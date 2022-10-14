package money.tegro.market.web.model

import browser.window
import kotlinx.coroutines.await
import money.tegro.market.web.wallet.TonWalletProvider
import org.w3c.dom.get

data class Connection(
    val provider: String? = null,
    val wallet: Wallet? = null,
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
                    wallet = it,
                )
            }
    }
}
