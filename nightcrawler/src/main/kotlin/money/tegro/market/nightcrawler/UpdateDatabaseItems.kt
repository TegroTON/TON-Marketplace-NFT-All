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

    private val itemMetadataUpdater: MetadataUpdater<ItemModel>,
    private val itemMetadataWriter: MetadataWriter<ItemModel, ItemRepository>,

    private val itemRoyaltyUpdater: RoyaltyUpdater<ItemModel>,
    private val itemRoyaltyWriter: RoyaltyWriter<ItemRepository>,
) {
    @Scheduled(initialDelay = "30s", fixedDelay = "5m")
    fun updateEverything() {
        logger.info { "Updating database items" }

        val updatedItems = itemRepository.findAll()
            .concatMap {
                if (it.dataUpdated?.let {
                        Duration.between(
                            it,
                            Instant.now()
                        ) < configuration.dataUpdateThreshold
                    } != false) {
                    itemUpdater.apply(it)
                } else {
                    it.toMono()
                }
            }
            .concatMap(itemUpdater)
            .publish()

        val a = updatedItems
            .subscribe(itemWriter)

        val b = updatedItems
            .filter {
                it.metadataUpdated?.let {
                    Duration.between(
                        it,
                        Instant.now()
                    ) < configuration.metadataUpdateThreshold
                } != false
            }
            .concatMap(itemMetadataUpdater)
            .subscribe(itemMetadataWriter)

        val c = updatedItems
            .filter {
                it.collection == null && // Only stand-alone items
                        it.royaltyUpdated?.let {
                            Duration.between(
                                it,
                                Instant.now()
                            ) < configuration.royaltyUpdateThreshold
                        } != false
            }
            .concatMap(itemRoyaltyUpdater)
            .subscribe(itemRoyaltyWriter)

        updatedItems.connect()

        while (!a.isDisposed && !b.isDisposed && !c.isDisposed) {
        }
    }

    companion object : KLogging()
}
