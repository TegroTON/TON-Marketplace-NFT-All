package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.data.model.Sort
import io.micronaut.kotlin.context.getBean
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.time.Instant

@Singleton
class CatchUpJob(
    private val context: ApplicationContext,
) {
    @Scheduled(initialDelay = "0s", fixedDelay = "\${money.tegro.market.nightcrawler.catchup-period:60m}")
    fun run() {
        runBlocking {
            logger.info { "Starting catching up" }
            val started = Instant.now()

            context.getEventPublisher(RefreshEvent::class.java)
                .publishEvent(RefreshEvent()) // To update the reference block

            context.getBean<LoadInitialCollections>().run()
            context.getBean<CatchUpOnCollections>().run()
            context.getBean<CatchUpOnItems>().run()

            logger.info { "Caught up in ${Duration.between(started, Instant.now())}" }
        }
    }

    @Prototype
    class LoadInitialCollections(
        private val resourceLoader: ClassPathResourceLoader,
        private val collectionRepository: CollectionRepository,
    ) {
        fun run() {
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
                CatchUpJob.logger.debug { "No file with initial collections found in the classpath" }
            }
        }

        companion object : KLogging()
    }

    @Prototype
    class CatchUpOnCollections(
        private val referenceBlock: FixedReferenceBlock,
        private val collectionRepository: CollectionRepository,
        private val itemRepository: ItemRepository,
        private val royaltyRepository: RoyaltyRepository,

        private val collectionDataProcess: CollectionDataProcess<FixedReferenceBlock>,
        private val collectionMetadataProcess: CollectionMetadataProcess,
        private val missingItemProcessor: MissingItemProcessor<FixedReferenceBlock>,

        private val royaltyProcess: RoyaltyProcess<FixedReferenceBlock>,
    ) {
        suspend fun run() {
            val seqno = referenceBlock()().seqno
            logger.info { "Updating collections up to block no. $seqno" }

            val collections = collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .replay()

            val data = collections
                .concatMap(collectionDataProcess)
                .concatMap(collectionMetadataProcess)
                .concatMap { collectionRepository.upsert(it) }

            val royalty = collections
                .map { it.address }
                .concatMap(royaltyProcess)
                .concatMap { royaltyRepository.upsert(it) }

            collections.connect()

            Mono.`when`(data, royalty)
                .awaitSingleOrNull()

            logger.info { "Discovering missing items" }
            collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                .concatMap(missingItemProcessor)
                .concatMap { itemRepository.save(it) }
                .then()
                .awaitSingleOrNull()

            logger.info { "Collections up-to-date at block height $seqno" }
        }

        companion object : KLogging()
    }

    @Prototype
    class CatchUpOnItems(
        private val referenceBlock: FixedReferenceBlock,

        private val itemRepository: ItemRepository,
        private val saleRepository: SaleRepository,
        private val royaltyRepository: RoyaltyRepository,

        private val itemDataProcess: ItemDataProcess<FixedReferenceBlock>,
        private val itemMetadataProcess: ItemMetadataProcess,

        private val saleProcess: SaleProcess<FixedReferenceBlock>,
        private val royaltyProcess: RoyaltyProcess<FixedReferenceBlock>,
    ) {
        suspend fun run() {
            val seqno = referenceBlock()().seqno
            logger.info { "Updating items up to block no. $seqno" }

            val items = itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .replay()

            val data = items
                .concatMap(itemDataProcess)
                .replay()

            val sale = data
                .concatMap {
                    it.owner?.let { saleProcess.apply(it) } ?: Mono.empty()
                }
                .concatMap { saleRepository.upsert(it) }

            val metadata = data
                .concatMap(itemMetadataProcess)
                .concatMap { itemRepository.upsert(it) }

            val royalty = items
                .filter { it.collection != null } // only stand-alone items
                .map { it.address }
                .concatMap(royaltyProcess)
                .concatMap { royaltyRepository.upsert(it) }

            data.connect()
            items.connect()

            Mono.`when`(data, sale, metadata, royalty).awaitSingleOrNull() // Wait for all of them to complete

            logger.info { "Items up-to-date at block height $seqno" }
        }

        companion object : KLogging()
    }

    companion object : KLogging()
}
