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
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.repository.RoyaltyRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient

@Singleton
open class EntityRoyaltyService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val royaltyRepository: RoyaltyRepository,
) : ApplicationEventListener<StartupEvent> {
    @Async
    open override fun onApplicationEvent(event: StartupEvent?) {
        runBlocking(Dispatchers.Default) {
            merge(
                // Watch live
                liveAccounts
                    .filter {
                        !collectionRepository.existsById(it) // Collections
                                || itemRepository.findById(it)
                            ?.let { it.collection is AddrNone } == true // Orphan items
                    }
                    .onEach {
                        logger.info("{} matched database entity", kv("address", it.toSafeBounceable()))
                    },
                // Apart from watching live interactions, update them periodically
                flow {
                    while (currentCoroutineContext().isActive) {
                        logger.debug("running scheduled update of all database entities")
                        collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                            .flatMapConcat {
                                itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                                    .filter { it.collection is AddrNone } // Orphan items
                                    .map { it.address }
                            }
                            .let { emitAll(it) }
                        delay(config.royaltyPeriod)
                    }
                }
            )
                // Work only on things that don't have a royalty entry yet
                .filter { !royaltyRepository.existsByAddress(it) }
                .mapNotNull {
                    logger.debug("querying royalty of {}", kv("address", it.toSafeBounceable()))
                    try {
                        val data = RoyaltyContract.of(it, liteClient)
                        RoyaltyModel(
                            address = it,
                            numerator = data.numerator,
                            denominator = data.denominator,
                            destination = data.destination,
                        )
                    } catch (e: ContractException) {
                        logger.debug("{} doesn't implement NFTRoyalty", kv("address", it.toSafeBounceable()), e)
                        null // Just skip on error
                    }
                }
                .collect { royaltyRepository.save(it) }
        }
    }

    companion object : KLogging()
}
