package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
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
open class MissingItemService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .concatMap { collectionRepository.findById(it) }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.missingItemPeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))) },
        )
            // All items with indexes 0 (incl.) to nextItemIndex (excl.)
            .concatMapIterable { collection ->
                (0 until collection.nextItemIndex)
                    .map { collection.address to it }
            }
            // Filter out existing items
            .filterWhen { itemRepository.existsByCollectionAndIndex(it.first, it.second).not() }
            // Get item address, if not AddrStd just skip it
            .concatMap {
                mono {
                    try {
                        CollectionContract.itemAddressOf(it.first, it.second, liteApi) as? AddrStd
                    } catch (e: ContractException) {
                        logger.warn(
                            "could not query item {} of collection {}",
                            kv("index", it.second),
                            kv("address", it.first.toSafeBounceable()),
                            e,
                        )
                        null // Just skip on error
                    }
                }
            }
            .filterWhen { itemRepository.existsById(it).not() }
            .concatMap {
                mono {
                    logger.debug("querying missing collection item {}", kv("address", it.toSafeBounceable()))

                    try {
                        val data = ItemContract.of(it, liteApi)
                        ItemModel(
                            address = it,
                            initialized = data.initialized,
                            index = data.index,
                            collection = data.collection,
                            owner = data.owner,
                            // Collection items have it set to true because then it will follow collection's status
                            // This enables us to hide specific items while leaving rest of the collection open
                            approved = (data.collection !is AddrNone),
                        )
                    } catch (e: ContractException) {
                        logger.warn("could not get item {}", kv("address", it), e)
                        null // Skip it as well
                    }
                }
            }
            .subscribe { itemRepository.save(it).subscribe() }
    }

    companion object : KLogging()
}
