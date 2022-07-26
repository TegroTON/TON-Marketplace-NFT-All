package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.CollectionRepository
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
open class CollectionService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val collectionRepository: CollectionRepository,
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
            Flux.interval(Duration.ZERO, config.collectionPeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
        )
            .concatMap {
                mono {
                    val data = CollectionContract.of(it.address, liteApi)
                    val metadata = CollectionMetadata.of(data.content)

                    it.copy(
                        nextItemIndex = data.nextItemIndex,
                        owner = data.owner,
                        name = metadata.name,
                        description = metadata.description,
                        image = metadata.image
                            ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                        coverImage = metadata.coverImage
                            ?: metadata.coverImageData?.let { "data:image;base64," + base64(it) },
                        updated = Instant.now(),
                    )
                }
            }
            .subscribe { collectionRepository.update(it).subscribe() }
    }

    companion object : KLogging()
}
