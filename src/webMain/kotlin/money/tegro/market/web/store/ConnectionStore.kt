package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.core.SimpleHandler
import kotlinx.coroutines.flow.Flow
import money.tegro.market.model.TransactionRequestModel
import org.kodein.di.conf.DIGlobalAware

abstract class ConnectionStore<T> : RootStore<T?>(null), DIGlobalAware {
    abstract val address: Flow<String?>
    abstract val isConnected: Flow<Boolean>
    abstract val isAvailable: Flow<Boolean>

    abstract val connect: SimpleHandler<Unit>
    abstract val disconnect: SimpleHandler<Unit>
    abstract val requestTransaction: SimpleHandler<TransactionRequestModel>
}
