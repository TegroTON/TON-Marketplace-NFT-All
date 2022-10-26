package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.repository.ResourceNotFoundException
import dev.fritz2.repository.localstorage.localStorageEntityOf
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.model.Connection
import money.tegro.market.web.model.TonWalletConnection
import money.tegro.market.web.resource.ConnectionResource
import org.kodein.di.conf.DIGlobalAware
import org.kodein.di.instance

class ConnectionStore : RootStore<Connection?>(null), DIGlobalAware {
    private val popOverStore: PopOverStore by instance()
    private val localStorage = localStorageEntityOf(ConnectionResource, "connection")

    val isConnected = data.map { it?.isConnected() == true }

    val load = handle { _ ->
        try {
            localStorage.load("connection")
        } catch (_: ResourceNotFoundException) {
            null
        }
    }
    val connect = handle<Connection> { _, connection ->
        connection.also {
            popOverStore.close()
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
        }
        load()
    }
}
