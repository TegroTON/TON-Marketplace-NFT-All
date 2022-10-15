package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import money.tegro.market.web.model.Connection

object ConnectionStore : RootStore<Connection>(Connection()) {
    val connectTonWallet = handle { _ ->
        DialogueStore.close()
        Connection.connectTonWallet() ?: Connection()
    }

    val connect = handle { connection ->
        DialogueStore.connect()
        connection
    }

    val disconnect = handle { _ ->
        Connection()
    }
}
