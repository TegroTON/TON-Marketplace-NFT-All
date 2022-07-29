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
import org.ton.lite.client.LiteClient

@Singleton
open class MissingItemService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : ApplicationEventListener<StartupEvent> {
    @Async
    open override fun onApplicationEvent(event: StartupEvent?) {
        runBlocking(Dispatchers.Default) {
            merge(
                // Watch live
                liveAccounts
                    .mapNotNull { collectionRepository.findById(it) }
                    .onEach {
                        logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                    },
                // Apart from watching live interactions, update them periodically
                flow {
                    while (currentCoroutineContext().isActive) {
                        logger.debug("running scheduled update of all database entities")
                        emitAll(collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))))
                        delay(config.missingItemPeriod)
                    }
                }
            )
                // All items with indexes 0 (incl.) to nextItemIndex (excl.)
                .flatMapConcat { collection ->
                    (0 until collection.nextItemIndex)
                        .map { collection.address to it }
                        .asFlow()
                }
                // Filter out existing items
                .filter { !itemRepository.existsByCollectionAndIndex(it.first, it.second) }
                // Get item address, if not AddrStd just skip it
                .mapNotNull {
                    try {
                        CollectionContract.itemAddressOf(it.first, it.second, liteClient) as? AddrStd
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
                .filter { !itemRepository.existsById(it) }
                .mapNotNull {
                    logger.debug("querying missing collection item {}", kv("address", it.toSafeBounceable()))

                    try {
                        val data = ItemContract.of(it, liteClient)
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
                .collect() { itemRepository.save(it) }
        }
    }

    companion object : KLogging()
}
