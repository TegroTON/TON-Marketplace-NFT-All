package money.tegro.market.nightcrawler

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import mu.KLogging
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.Instant

@Singleton
class UpdateDatabaseItems(
    private val configuration: UpdateDatabaseItemsConfiguration,
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
) {
    @Scheduled(initialDelay = "30s", fixedDelay = "5m")
    fun updateEverything() {
        logger.info { "Updating database items" }

        val updatedItems = itemRepository.findAll()
            .concatMap {
                if (Duration.between(it.updated, Instant.now()) < configuration.dataUpdateThreshold) {
                    itemUpdater.apply(it)
                } else {
                    it.toMono()
                }
            }
            .concatMap(itemUpdater)
            .publish()

        val a = updatedItems
            .subscribe(itemWriter)

        val updatedMetadata = updatedItems
            .filter { Duration.between(it.metadataUpdated, Instant.now()) < configuration.metadataUpdateThreshold }
            .concatMap(metadataFetcher)
            .publish()

        val b = updatedMetadata
            .concatMap(metadataUpdater)
            .subscribe(metadataWriter)

        val c = updatedMetadata
            .concatMap(attributeUpdater)
            .subscribe(attributeWriter)

        val d = updatedItems
            .filter {
                it.collection == null && // Only stand-alone items
                        Duration.between(it.royaltyUpdated, Instant.now()) < configuration.royaltyUpdateThreshold
            }
            .concatMap(royaltyUpdater)
            .subscribe(royaltyWriter)

        updatedItems.connect()
        updatedMetadata.connect()

        while (!a.isDisposed && !b.isDisposed && !c.isDisposed && !d.isDisposed) {
        }
    }

    companion object : KLogging()
}
