package money.tegro.market.nightcrawler

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import mu.KLogging

@Singleton
class UpdateDatabaseItems(
    private val itemRepository: ItemRepository,

    private val itemUpdater: ItemUpdater,
    private val itemWriter: ItemWriter,

    private val itemMetadataUpdater: MetadataUpdater<ItemModel>,
    private val itemMetadataWriter: MetadataWriter<ItemRepository>,

    private val itemRoyaltyUpdater: RoyaltyUpdater<ItemModel>,
    private val itemRoyaltyWriter: RoyaltyWriter<ItemRepository>,
) {
    @Scheduled(initialDelay = "30s", fixedDelay = "5m")
    fun updateEverything() {
        logger.info { "Updating database items" }

        val updatedItems = itemRepository.findAll()
            .concatMap(itemUpdater)
            .publish()

        val a = updatedItems
            .subscribe(itemWriter)

        val b = updatedItems
            .concatMap(itemMetadataUpdater)
            .subscribe(itemMetadataWriter)

        val c = updatedItems
            .filter { it.collection == null }  // Only stand-alone items
            .concatMap(itemRoyaltyUpdater)
            .subscribe(itemRoyaltyWriter)

        updatedItems.connect()

        while (!a.isDisposed && !b.isDisposed && !c.isDisposed) {
        }
    }

    companion object : KLogging()
}
