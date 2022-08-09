package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.delay
import money.tegro.market.config.ServiceConfig
import money.tegro.market.core.toRaw
import money.tegro.market.repository.AccountRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient
import java.time.Instant
import kotlin.coroutines.CoroutineContext

@Singleton
open class AccountService(
    private val liteClient: LiteClient,
    private val config: ServiceConfig,

    private val liveAccounts: Flow<AddrStd>,

    private val accountRepository: AccountRepository,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    @EventListener
    open fun onStartup(event: StartupEvent) {
    }

    @PostConstruct
    open fun onInit() {
        job.start()
    }

    @PreDestroy
    open fun onShutdown() {
        job.cancel()
    }

    private val job = launch {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { accountRepository.findById(it) }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toRaw()))
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
//                    accountRepository.findAll().collect { send(it) }
                    delay(config.accountPeriod)
                }
            }
        )
            .collect {
                // Nothing to do here as of yet, just update the date ig
                accountRepository.update(it.copy(updated = Instant.now()))
            }
    }

    companion object : KLogging()

}
