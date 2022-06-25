package money.tegro.market.nightcrawler.jobs

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTDeployedCollection
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.existsByAddressStd
import money.tegro.market.nightcrawler.MetadataFetcher
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.updater.CollectionUpdater
import money.tegro.market.nightcrawler.updater.MetadataUpdater
import money.tegro.market.nightcrawler.updater.RoyaltyUpdater
import money.tegro.market.nightcrawler.writer.CollectionWriter
import money.tegro.market.nightcrawler.writer.MetadataWriter
import money.tegro.market.nightcrawler.writer.RoyaltyWriter
import mu.KLogging
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Singleton
class DatabaseCollectionJobs(
    private val configuration: NightcrawlerConfiguration,

    private val resourceLoader: ClassPathResourceLoader,
    private val liteApi: LiteApi,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,

    private val collectionUpdater: CollectionUpdater,
    private val collectionWriter: CollectionWriter,

    private val metadataFetcher: MetadataFetcher<CollectionModel>,
    private val metadataUpdater: MetadataUpdater<CollectionModel>,
    private val metadataWriter: MetadataWriter<CollectionModel, CollectionRepository>,

    private val royaltyUpdater: RoyaltyUpdater<CollectionModel>,
    private val royaltyWriter: RoyaltyWriter<CollectionRepository>,
) {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    fun initializeCollections() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .filter { it.isNotBlank() }
                .map { AddrStd(it) }
                .filter { !collectionRepository.existsByAddressStd(it) }
                .forEach {
                    collectionRepository.save(CollectionModel(it)).subscribe()
                }
        } ?: run {
            logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    @Scheduled(initialDelay = "0s")
    fun updateEverything() {
        initializeCollections()

        logger.info { "Updating database collections" }

        val updatedCollections =
            Flux.interval(Duration.ZERO, configuration.collectionUpdatePeriod)
                .flatMap { collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
                .onBackpressureBuffer(configuration.backpressureBufferSize, BufferOverflowStrategy.DROP_OLDEST)
                .publishOn(Schedulers.boundedElastic())
                .concatMap(collectionUpdater)
                .publish()

        updatedCollections
            .subscribe(collectionWriter)

        updatedCollections
            .concatMap(metadataFetcher)
            .concatMap(metadataUpdater)
            .subscribe(metadataWriter)

        updatedCollections
            .concatMap(royaltyUpdater)
            .subscribe(royaltyWriter)

        updatedCollections.connect()

        logger.info { "Going dark" }
    }

    @Scheduled(initialDelay = "0s")
    fun discoverMissingItems() {
        logger.info { "Discovering missing collection items" }

        Flux.interval(Duration.ZERO, configuration.missingItemsDiscoveryPeriod)
            .flatMap { collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
            .onBackpressureBuffer(configuration.backpressureBufferSize, BufferOverflowStrategy.DROP_OLDEST)
            .concatMap { collection ->
                collection.nextItemIndex?.let { nextItemIndex ->
                    (0 until nextItemIndex).toFlux()
                        .filterWhen {
                            // Ignore items that are already added and indexed
                            itemRepository.existsByIndexAndCollection(it, collection.address).map { !it }
                        }
                        .map {
                            collection.address.to() to it
                        }
                } ?: Flux.empty()
            }
            .publishOn(Schedulers.boundedElastic())
            .concatMap {
                mono {
                    it.first to NFTDeployedCollection.itemAddressOf(it.first, it.second, liteApi)
                }
            }
            .filter { !itemRepository.existsByAddressStd(it.second) }
            .subscribe {
                itemRepository.save(ItemModel(it.second).apply { collection = AddressKey.of(it.first) }).subscribe()
            }

        logger.info { "Going dark" }
    }

    companion object : KLogging()
}
