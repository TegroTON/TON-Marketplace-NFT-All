package money.tegro.market.nightcrawler

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import mu.KLogging
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.time.Instant

@Singleton
class CatchupJobs(
    private val resourceLoader: ClassPathResourceLoader,

    private val referenceBlock: ReferenceBlock,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,

    private val updateCollectionData: UpdateCollectionData,
    private val updateCollectionMetadata: UpdateCollectionMetadata,
    private val discoverMissingItems: DiscoverMissingItems,

    private val updateItemData: UpdateItemData,
    private val updateSaleData: UpdateSaleData,
    private val updateItemMetadata: UpdateItemMetadata,

    private val updateRoyalty: UpdateRoyalty,
) {
    @Scheduled(initialDelay = "0s", fixedDelay = "1h")
    fun run() {
        runBlocking {
            logger.info { "Starting catching up" }
            val started = Instant.now()

            loadInitialCollections()
            catchupOnCollections()
            catchupOnItems()

            logger.info { "Caught up in ${Duration.between(started, Instant.now())}" }
        }
    }

    private suspend fun loadInitialCollections() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .toFlux()
                .filter { it.isNotBlank() }
                .map { AddrStd(it) }
                .filterWhen { collectionRepository.existsByAddress(it).map { !it } }
                .concatMap {
                    collectionRepository.save(CollectionModel(it))
                }
                .blockLast()
        } ?: run {
            logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    private suspend fun catchupOnCollections() {
        val seqno = referenceBlock.get().invoke().seqno
        logger.info { "Updating collections up to block no. $seqno" }

        val collections = collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .publishOn(Schedulers.boundedElastic())
            .replay()

        val data = collections
            .map { it.address }
            .concatMap(updateCollectionData)
            .replay()

        val metadata = data
            .concatMap(updateCollectionMetadata)

        val royalty = collections
            .map { it.address }
            .concatMap(updateRoyalty)

        data.connect()
        collections.connect()

        Mono.`when`(data, metadata, royalty)
            .awaitSingleOrNull()

        logger.info { "Discovering missing items" }
        collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .map { it.address to (it.nextItemIndex ?: 0) }
            .concatMap(discoverMissingItems)
            .then()
            .awaitSingleOrNull()

        logger.info { "Collections up-to-date at block height $seqno" }
    }

    private suspend fun catchupOnItems() {
        val seqno = referenceBlock.get().invoke().seqno
        logger.info { "Updating items up to block no. $seqno" }

        val items = itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .publishOn(Schedulers.boundedElastic())
            .replay()

        val data = items
            .map { it.address }
            .concatMap(updateItemData)
            .replay()

        val sale = data
            .concatMap {
                it.owner?.let { updateSaleData.apply(it) } ?: Mono.empty()
            }

        val metadata = data
            .concatMap(updateItemMetadata)

        val royalty = items
            .filter { it.collection != null } // only stand-alone items
            .map { it.address }
            .concatMap(updateRoyalty)

        items.connect()
        data.connect()

        Mono.`when`(data, sale, metadata, royalty).awaitSingleOrNull() // Wait for all of them to complete

        logger.info { "Items up-to-date at block height $seqno" }
    }

    companion object : KLogging()
}
