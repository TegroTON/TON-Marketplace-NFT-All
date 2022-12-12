package money.tegro.market.web.store

import dev.fritz2.core.SimpleHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import money.tegro.market.model.TransactionRequestModel
import org.kodein.di.instance

@OptIn(FlowPreview::class)
class GlobalConnectionStore : ConnectionStore<ConnectionStore<Any>>() {
    private val tonWalletConnectionStore: TonWalletConnectionStore by di.instance()
    private val tonkeeperConnectionStore: TonkeeperConnectionStore by di.instance()

    override val address: Flow<String?> = data.flatMapConcat { it?.address ?: emptyFlow() }

    override val isAvailable: Flow<Boolean> = data.flatMapConcat { it?.isAvailable ?: flowOf(false) }

    override val isConnected: Flow<Boolean> = data.flatMapConcat { it?.isConnected ?: flowOf(false) }

    override val connect: SimpleHandler<Unit> = handle { connection ->
        connection?.let { it.connect() }
        connection
    }

    override val disconnect: SimpleHandler<Unit> = handle { connection ->
        connection?.let { it.disconnect() }
        connection
    }

    override val requestTransaction: SimpleHandler<TransactionRequestModel> = handle { connection, request ->
        connection?.requestTransaction(request)
        connection
    }
}
