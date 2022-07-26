package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant

@Singleton
open class ItemService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val itemRepository: ItemRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .concatMap { itemRepository.findById(it) }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.itemPeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { itemRepository.findAll(Sort.of(Sort.Order.asc("updated"))) },
        )
            .concatMap {
                mono {
                    logger.debug("updating item {}", kv("address", it.address.toSafeBounceable()))
                    val data = ItemContract.of(it.address, liteApi)
                    val metadata = ItemMetadata.of(
                        (data.collection as? AddrStd)
                            ?.let { CollectionContract.itemContent(it, data.index, data.individualContent, liteApi) }
                            ?: data.individualContent
                    )

                    it.copy(
                        initialized = data.initialized,
                        index = data.index,
                        collection = data.collection,
                        owner = data.owner,
                        name = metadata.name,
                        description = metadata.description,
                        image = metadata.image
                            ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                        updated = Instant.now(),
                    )
                }
            }
            .subscribe { itemRepository.update(it).subscribe() }
    }

    companion object : KLogging()
}
