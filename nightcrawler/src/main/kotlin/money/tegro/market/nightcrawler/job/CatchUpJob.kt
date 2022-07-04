package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.data.model.Sort
import io.micronaut.kotlin.context.getBean
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.not
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

            val referenceBlock = context.getBean<LiteApi>().getMasterchainInfo().last

            context.getBean<LoadInitialCollections>().run { referenceBlock }
            context.getBean<CatchUpOnCollections>().run { referenceBlock }
            context.getBean<CatchUpOnItems>().run { referenceBlock }

            logger.info { "Caught up in ${Duration.between(started, Instant.now())}" }
        }
    }

    @Prototype
    class LoadInitialCollections(
        private val liteApi: LiteApi,
        private val resourceLoader: ClassPathResourceLoader,
        private val collectionRepository: CollectionRepository,
    ) {
        suspend fun run(referenceBlock: suspend () -> TonNodeBlockIdExt) {
            logger.info { "Loading initial collections" }

            val resource = resourceLoader.classLoader.getResource("init_collections.csv")

            if (resource == null) {
                logger.warn { "No file with initial collections was found in the classpath" }
                return
            } else {
                resource.readText()
                    .lineSequence()
                    .toFlux()
                    .filter { it.isNotBlank() }
                    .map { AddrStd(it) }
                    .filterWhen { collectionRepository.existsByAddress(it).not() }
                    .concatMap {
                        mono {
                            val collection = NFTCollection.of(it, liteApi, referenceBlock)
                            CollectionModel.of(collection, collection.metadata())
                        }
                    }
                    .doOnNext { collectionRepository.save(it).subscribe() }
                    .then()
                    .awaitSingleOrNull()

                logger.info { "Alles gute!" }
            }
        }

        companion object : KLogging()
    }

    @Prototype
    class CatchUpOnCollections(
        private val collectionRepository: CollectionRepository,
        private val itemRepository: ItemRepository,
        private val royaltyRepository: RoyaltyRepository,

        private val collectionProcess: CollectionProcess,
        private val royaltyProcess: RoyaltyProcess,
        private val missingItemsProcess: MissingItemsProcess,
    ) {
        suspend fun run(referenceBlock: suspend () -> TonNodeBlockIdExt) {
            val seqno = referenceBlock.invoke().seqno

            logger.info { "Updating collections up to block no. $seqno" }
            collectionRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(collectionProcess(referenceBlock)) // Data and metadata
                .doOnNext { collectionRepository.upsert(it).subscribe() }
                .doOnNext { // Royalty
                    it.address.toMono()
                        .flatMap(royaltyProcess(referenceBlock))
                        .onErrorStop() // If it doesn't implement royalty extension
                        .subscribe { royaltyRepository.upsert(it).subscribe() }
                }
                .then()
                .awaitSingleOrNull()

            logger.info { "Discovering missing items" }
            collectionRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(missingItemsProcess(referenceBlock))
                .doOnNext { itemRepository.save(it).subscribe() }
                .then()
                .awaitSingleOrNull()

            logger.info { "Collections up-to-date at block height $seqno" }
        }

        companion object : KLogging()
    }

    @Prototype
    class CatchUpOnItems(
        private val itemRepository: ItemRepository,
        private val saleRepository: SaleRepository,
        private val royaltyRepository: RoyaltyRepository,

        private val itemProcess: ItemProcess,
        private val royaltyProcess: RoyaltyProcess,
        private val saleProcess: SaleProcess,
    ) {
        suspend fun run(referenceBlock: suspend () -> TonNodeBlockIdExt) {
            val seqno = referenceBlock.invoke().seqno

            logger.info { "Updating items up to block no. $seqno" }
            itemRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(itemProcess(referenceBlock)) // Data and metadata
                .doOnNext { itemRepository.upsert(it).subscribe() }
                .doOnNext { // Royalty
                    if (it.collection != null)
                        it.address.toMono()
                            .flatMap(royaltyProcess(referenceBlock))
                            .onErrorStop() // If it doesn't implement royalty extension
                            .subscribe { royaltyRepository.upsert(it).subscribe() }
                }
                .doOnNext { // Sale
                    it.address.toMono()
                        .flatMap(saleProcess(referenceBlock))
                        .onErrorStop() // If not a sale contract
                        .subscribe { saleRepository.upsert(it).subscribe() }
                }
                .then()
                .awaitSingleOrNull()

            logger.info { "Items up-to-date at block height $seqno" }
        }

        companion object : KLogging()
    }

    companion object : KLogging()
}
