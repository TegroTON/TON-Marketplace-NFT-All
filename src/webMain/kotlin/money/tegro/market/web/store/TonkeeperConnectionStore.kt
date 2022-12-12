package money.tegro.market.web.store

import Account
import TonConnect
import WalletConnectionSourceHTTP
import dev.fritz2.core.SimpleHandler
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.js.jso
import money.tegro.market.model.TransactionRequestModel

class TonkeeperConnectionStore : ConnectionStore<Account>() {
    override val address: Flow<String?> = data.map { it?.address }

    override val isAvailable: Flow<Boolean> = data.map { true }

    override val isConnected: Flow<Boolean> =
        data.map { connector.connected }

    override val connect: SimpleHandler<Unit> = handle { account ->
        account
    }

    override val disconnect: SimpleHandler<Unit> = handle { _ ->
        connector.disconnect().await()
        null
    }

    override val requestTransaction: SimpleHandler<TransactionRequestModel> = handle { wallet, request ->
        TODO()
    }

    companion object {
        val connector = jso<TonConnect>()

        fun connectLink() = connector.connect(object : WalletConnectionSourceHTTP {
            override var universalLink: String = "https://app.tonkeeper.com/ton-connect"
            override var bridgeUrl: String = "https://bridge.tonapi.io/bridge"
        }).unsafeCast<String?>()
    }
}
