package money.tegro.market.service

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.delay
import money.tegro.market.config.ServiceConfig
import money.tegro.market.core.toRaw
import money.tegro.market.model.accounts
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.forEach
import org.ktorm.entity.sortedBy
import org.ktorm.entity.update
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import org.ton.block.AddrStd
import java.time.Instant
import kotlin.coroutines.CoroutineContext

@Service
class AccountService(
    private val config: ServiceConfig,

    private val liveAccounts: Flow<AddrStd>,

    private val database: Database,
) : CoroutineScope, ApplicationListener<ApplicationStartedEvent>, InitializingBean, DisposableBean {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
    }

    override fun afterPropertiesSet() {
        liveJob.start()
    }

    override fun destroy() {
        liveJob.cancel()
    }

    private val liveJob = launch {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { address -> database.accounts.find { it.address eq address } }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toRaw()))
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    database.accounts.sortedBy { it.updated }.forEach { send(it) }
                    delay(config.accountPeriod)
                }
            }
        )
            .collect {
                // Nothing to do here as of yet, just update the date ig
                database.accounts.update(it.apply { updated = Instant.now() })
            }
    }

    companion object : KLogging()
}
