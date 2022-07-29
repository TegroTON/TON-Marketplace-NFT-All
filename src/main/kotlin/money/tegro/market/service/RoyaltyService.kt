package money.tegro.market.service

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.RoyaltyRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient
import java.time.Instant

@Singleton
open class RoyaltyService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val royaltyRepository: RoyaltyRepository,
) : ApplicationEventListener<StartupEvent> {
    @Async
    open override fun onApplicationEvent(event: StartupEvent?) {
        runBlocking(Dispatchers.Default) {
            merge(
                // Watch live
                liveAccounts
                    .mapNotNull { royaltyRepository.findById(it) }
                    .onEach {
                        logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                    },
                // Apart from watching live interactions, update them periodically
                flow {
                    while (currentCoroutineContext().isActive) {
                        logger.debug("running scheduled update of all database entities")
                        emitAll(royaltyRepository.findAll(Sort.of(Sort.Order.asc("updated"))))
                        delay(config.royaltyPeriod)
                    }
                }
            )
                .mapNotNull {
                    logger.debug("updating royalty {}", kv("address", it.address.toSafeBounceable()))
                    try {
                        val data = RoyaltyContract.of(it.address, liteClient)
                        it.copy(
                            numerator = data.numerator,
                            denominator = data.denominator,
                            destination = data.destination,
                            updated = Instant.now(),
                        )
                    } catch (e: ContractException) {
                        logger.warn("could not get royalty for {}, removing entry", kv("address", it), e)
                        royaltyRepository.delete(it)
                        null
                    }
                }
                .collect { royaltyRepository.update(it) }
        }
    }

    companion object : KLogging()
}
