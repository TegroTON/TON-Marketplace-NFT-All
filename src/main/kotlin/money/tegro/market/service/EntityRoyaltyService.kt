package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
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
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.extra.bool.not
import java.time.Duration

@Singleton
open class EntityRoyaltyService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val royaltyRepository: RoyaltyRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .filterWhen {
                    collectionRepository.existsById(it) // Collections
                        .or(itemRepository.findById(it).map { it.collection is AddrNone }) // Orphan items
                }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.royaltyPeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap {
                    // All collections
                    collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                        .map { it.address }
                        .concatWith(
                            itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                                .filter { it.collection is AddrNone } // Orphan items
                                .map { it.address }
                        )
                }
        )
            // Work only on things that don't have a royalty entry yet
            .filterWhen { royaltyRepository.existsByAddress(it).not() }
            .concatMap {
                mono {
                    logger.debug("querying royalty of {}", kv("address", it.toSafeBounceable()))
                    try {
                        val data = RoyaltyContract.of(it, liteApi)
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
            }
            .subscribe { royaltyRepository.save(it).subscribe() }
    }

    companion object : KLogging()
}
