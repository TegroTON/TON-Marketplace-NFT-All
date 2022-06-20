package money.tegro.market.nightcrawler

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTDeployedCollection
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.addressStd
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.existsByAddressStd
import mu.KLogging
import org.ton.block.MsgAddressIntStd
import org.ton.lite.api.LiteApi
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux

@Singleton
class UpdateDatabaseCollections(
    private val resourceLoader: ClassPathResourceLoader,
    private val liteApi: LiteApi,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,

    private val collectionUpdater: CollectionUpdater,
    private val collectionWriter: CollectionWriter,

    private val metadataUpdater: MetadataUpdater<CollectionModel>,
    private val metadataWriter: MetadataWriter<CollectionRepository>,

    private val royaltyUpdater: RoyaltyUpdater<CollectionModel>,
    private val royaltyWriter: RoyaltyWriter<CollectionRepository>,
) {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    @Scheduled(initialDelay = "0s")
    fun initializeCollections() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .filter { it.isNotBlank() }
                .map { MsgAddressIntStd(it) }
                .filter { !collectionRepository.existsByAddressStd(it) }
                .forEach {
                    collectionRepository.save(CollectionModel(it)).subscribe()
                }
        } ?: run {
            logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    @Scheduled(initialDelay = "5s", fixedDelay = "5m")
    fun updateEverything() {
        logger.info { "Updating database collections" }

        val updatedCollections = collectionRepository.findAll()
            .concatMap(collectionUpdater)
            .replay()

        val a = updatedCollections
            .subscribe(collectionWriter)

        val b = updatedCollections
            .concatMap(metadataUpdater)
            .subscribe(metadataWriter)

        val c = updatedCollections
            .concatMap(royaltyUpdater)
            .subscribe(royaltyWriter)

        updatedCollections.connect()

        while (!a.isDisposed && !b.isDisposed && !c.isDisposed) {
        }
    }

    @Scheduled(initialDelay = "10s")
    fun discoverMissingItems() {
        logger.info { "Discovering missing collection items" }

        collectionRepository.findAll()
            .concatMap { collection ->
                collection.nextItemIndex?.let { nextItemIndex ->
                    (0 until nextItemIndex).toFlux()
                        // Ignore items that are already added and indexed
                        .filter { !itemRepository.existsByIndexAndCollection(it, collection) }
                        .map { collection to it }
                }
            }
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .concatMap {
                mono {
                    it.first to NFTDeployedCollection.itemAddressOf(it.first.addressStd(), it.second, liteApi)
                }
            }
            .filter { !itemRepository.existsByAddressStd(it.second) }
            .subscribe {
                itemRepository.save(ItemModel(it.second).apply { collection = it.first }).subscribe()
            }
    }

    companion object : KLogging()
}
