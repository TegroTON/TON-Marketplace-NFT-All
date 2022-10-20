package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.repository.ResourceNotFoundException
import dev.fritz2.repository.localstorage.localStorageEntityOf
import kotlinx.coroutines.flow.drop
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.model.Connection
import money.tegro.market.web.model.TonWalletConnection
import money.tegro.market.web.resource.ConnectionResource

object ConnectionStore : RootStore<Connection?>(null) {
    object isConnected : RootStore<Boolean>(false) {
        val load = handle { _ ->
            ConnectionStore.current?.isConnected() ?: false
        }
    }

    private val localStorage = localStorageEntityOf(ConnectionResource, "connection")

    val load = handle { _ ->
        try {
            localStorage.load("connection")
        } catch (_: ResourceNotFoundException) {
            null
        }

    }
    val connect = handle<Connection> { _, connection ->
        connection.also {
            PopOverStore.close()
        }
    }

    val connectTonWallet = handle { _ ->
        (TonWalletConnection.connect())
            ?.also {
                connect(it)
            }
    }

    val disconnect = handle { _ ->
        current?.let { localStorage.delete(it) }
        null
    }

    val requestTransaction = handle<TransactionRequestModel> { _, request ->
        current?.requestTransaction(request)
        current
    }

    init {
        data.drop(1) handledBy { connection ->
            if (connection != null) {
                localStorage.addOrUpdate(connection)
            }

            isConnected.load()
        }
        load()
    }
}
