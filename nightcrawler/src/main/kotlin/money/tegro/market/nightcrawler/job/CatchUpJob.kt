package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.data.model.Sort
import io.micronaut.kotlin.context.getBean
import io.micronaut.scheduling.annotation.Scheduled
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.not
import java.time.Duration
import java.time.Instant

@Prototype
class CatchUpJob(
    private val context: ApplicationContext,
) {
    @Scheduled(initialDelay = "1s", fixedDelay = "\${money.tegro.market.nightcrawler.catchup-period:60m}")
    fun run() {
        runBlocking {
            val started = Instant.now()
            logger.info { "catch-up job started" }

            val referenceBlock = context.getBean<LiteApi>().getMasterchainInfo().last
            context.getBean<LoadInitialCollections>().run { referenceBlock }
            context.getBean<CatchUpOnCollections>().run { referenceBlock }
            context.getBean<CatchUpOnItems>().run { referenceBlock }


            logger.info("caught up in {}", value("catchUpTime", Duration.between(started, Instant.now())))
        }
    }

    @Prototype
    class LoadInitialCollections(
        private val liteApi: LiteApi,
        private val resourceLoader: ClassPathResourceLoader,
        private val collectionRepository: CollectionRepository,
    ) {
        suspend fun run(referenceBlock: suspend () -> TonNodeBlockIdExt) {
            logger.info { "loading initial collections" }

            val resource = resourceLoader.classLoader.getResource("init_collections.csv")

            if (resource == null) {
                logger.warn { "no file with initial collections was found in the classpath" }
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
                            logger.debug("processing collection {}", value("address", it.toSafeBounceable()))
                            val collection = NFTCollection.of(it, liteApi, referenceBlock)
                            CollectionModel.of(collection, collection.metadata())
                        }
                    }
                    .doOnNext {
                        collectionRepository.save(it).subscribe {}
                    }
                    .then()
                    .awaitSingleOrNull()

                logger.info { "alles gute!" }
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
            logger.info("updating collections up to the reference block no. {}", value("seqno", referenceBlock().seqno))
            collectionRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(collectionProcess(referenceBlock)) // Data and metadata
                .doOnNext { collectionRepository.upsert(it).subscribe() }
                .doOnNext { // Royalty
                    it.address.toMono()
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(royaltyProcess(referenceBlock))
                        .onErrorStop() // If it doesn't implement royalty extension
                        .subscribe { royaltyRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .then()
                .awaitSingleOrNull()

            logger.info { "discovering missing items" }
            collectionRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(missingItemsProcess(referenceBlock))
                .doOnNext { itemRepository.save(it).subscribeOn(Schedulers.single()).subscribe() }
                .then()
                .awaitSingleOrNull()

            logger.info("collections up-to-date at block no. {}", value("seqno", referenceBlock().seqno))
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
            logger.info("updating items up to the reference block no. {}", value("seqno", referenceBlock().seqno))
            itemRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .concatMap(itemProcess(referenceBlock)) // Data and metadata
                .doOnNext { itemRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                .doOnNext { // Royalty
                    if (it.collection != null)
                        it.address.toMono()
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(royaltyProcess(referenceBlock))
                            .onErrorStop() // If it doesn't implement royalty extension
                            .subscribe { royaltyRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .doOnNext { // Sale
                    it.address.toMono()
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(saleProcess(referenceBlock))
                        .onErrorStop() // If not a sale contract
                        .subscribe { saleRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .then()
                .awaitSingleOrNull()

            logger.info { "updating sales up to reference block" }
            saleRepository
                .findAll(Sort.of(Sort.Order.asc("updated")))
                .publishOn(Schedulers.boundedElastic())
                .map { it.address }
                .flatMap(saleProcess(referenceBlock))
                .doOnError {// Failed to get info for this address, remove it from the db
                    saleRepository.deleteById((Exceptions.unwrap(it) as ProcessException).id)
                        .subscribeOn(Schedulers.single()).subscribe()
                }
                .doOnNext {
                    saleRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe()
                }
                .then()
                .awaitSingleOrNull()

            logger.info("items up-to-date at block no. {}", value("seqno", referenceBlock().seqno))
        }

        companion object : KLogging()
    }

    companion object : KLogging()
}
