package money.tegro.market.nightcrawler.jobs

import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.nightcrawler.MetadataFetcher
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.updater.*
import money.tegro.market.nightcrawler.writer.*
import mu.KLogging
import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.Instant

@Singleton
class DatabaseItemJobs(
    private val configuration: NightcrawlerConfiguration,
    private val itemRepository: ItemRepository,

    private val itemUpdater: ItemUpdater,
    private val itemWriter: ItemWriter,

    private val metadataFetcher: MetadataFetcher<ItemModel>,
    private val metadataUpdater: MetadataUpdater<ItemModel>,
    private val metadataWriter: MetadataWriter<ItemModel, ItemRepository>,

    private val attributeUpdater: AttributeUpdater,
    private val attributeWriter: AttributeWriter,

    private val royaltyUpdater: RoyaltyUpdater<ItemModel>,
    private val royaltyWriter: RoyaltyWriter<ItemRepository>,

    private val saleUpdater: SaleUpdater,
    private val saleWriter: SaleWriter,
) {
    @Scheduled(initialDelay = "0s")
    fun updateEverything() {
        logger.info { "Updating database items" }

        val updatedItems =
            Flux.interval(Duration.ZERO, configuration.itemUpdatePeriod)
                .flatMap { itemRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
                .onBackpressureBuffer(configuration.backpressureBufferSize, BufferOverflowStrategy.DROP_OLDEST)
                .publishOn(Schedulers.boundedElastic())
                .concatMap {
                    if (Duration.between(it.updated, Instant.now()) < configuration.dataUpdateThreshold) {
                        itemUpdater.apply(it)
                    } else {
                        it.toMono()
                    }
                }
                .publish()

        updatedItems
            .subscribe(itemWriter)

        val updatedMetadata = updatedItems
            .filter { Duration.between(it.metadataUpdated, Instant.now()) < configuration.metadataUpdateThreshold }
            .concatMap(metadataFetcher)
            .publish()

        updatedMetadata
            .concatMap(metadataUpdater)
            .subscribe(metadataWriter)

        updatedMetadata
            .concatMap(attributeUpdater)
            .subscribe(attributeWriter)

        updatedItems
            .filter {
                it.collection == null && // Only stand-alone items
                        Duration.between(it.royaltyUpdated, Instant.now()) < configuration.royaltyUpdateThreshold
            }
            .concatMap(royaltyUpdater)
            .subscribe(royaltyWriter)

        updatedItems
            .concatMap { mono { it.address.to() } }
            .concatMap(saleUpdater)
            .subscribe(saleWriter)

        updatedItems.connect()
        updatedMetadata.connect()

        logger.info { "Going dark" }
    }

    companion object : KLogging()
}
