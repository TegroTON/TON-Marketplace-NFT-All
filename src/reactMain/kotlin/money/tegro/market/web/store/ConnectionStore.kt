package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.repository.ResourceNotFoundException
import dev.fritz2.repository.localstorage.localStorageEntityOf
import money.tegro.market.web.model.Connection
import money.tegro.market.web.resource.ConnectionResource

object ConnectionStore : RootStore<Connection>(Connection()) {
    private val localStorage = localStorageEntityOf(ConnectionResource, "connection")

    val load = handle { _ ->
        try {
            localStorage.load("connection")
        } catch (_: ResourceNotFoundException) {
            Connection()
        }
    }

    val connect = handle<Connection> { _, connection ->
        connection.also {
            localStorage.addOrUpdate(it)
            ConnectModalStore.update(false)
        }
    }

    val connectTonWallet = handle { _ ->
        (Connection.connectTonWallet() ?: Connection())
            .also {
                connect(it)
            }
    }

    val disconnect = handle { old ->
        Connection().also {
            localStorage.delete(old)
        }
    }

    init {
        load()
    }
}
