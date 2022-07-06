package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.keyValue
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Singleton
class LiveJob(
    private val context: ApplicationContext,
    private val liteApi: LiteApi,

    private val itemRepository: ItemRepository,
    private val collectionRepository: CollectionRepository,
    private val saleRepository: SaleRepository,
    private val royaltyRepository: RoyaltyRepository,

    private val collectionProcess: CollectionProcess,
    private val itemProcess: ItemProcess,
    private val missingItemsProcess: MissingItemsProcess,
    private val royaltyProcess: RoyaltyProcess,
    private val saleProcess: SaleProcess,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "Remember, use your zoom, steady hands." }

            val affectedAccounts =
                Flux.interval(Duration.ofSeconds(2))
                    .flatMap { mono { liteApi.getMasterchainInfo().last } }
                    .distinctUntilChanged()
                    .flatMap {
                        mono {
                            try {
                                logger.debug(
                                    "getting masterchain block no. {}", value("seqno", it.seqno)
                                )
                                liteApi.getBlock(it).dataBagOfCells().roots.first().parse {
                                    Block.TlbCombinator.loadTlb(this)
                                }
                            } catch (e: Exception) {
                                logger.warn("failed to get masterchain block no. {}", value("seqno", it.seqno), e)
                                null
                            }
                        }
                    }
                    .flatMap {
                        it.extra.custom.value
                            ?.shard_hashes
                            ?.toMap()
                            ?.asSequence()
                            .orEmpty()
                            .flatMap {
                                val (key, value) = it
                                val workchain = BigInt(key.toByteArray()).toInt()
                                value.nodes().map { workchain to it }
                            }
                            .toFlux()
                            .flatMap {
                                mono {
                                    val (workchain, descr) = it
                                    logger.debug(
                                        "getting shard {} block no. {}",
                                        keyValue("workchain", workchain),
                                        value("seqno", descr.seq_no)
                                    )
                                    liteApi.getBlock(
                                        TonNodeBlockIdExt(
                                            workchain = workchain,
                                            shard = descr.next_validator_shard,
                                            seqno = descr.seq_no.toInt(),
                                            rootHash = descr.root_hash.toByteArray(),
                                            fileHash = descr.file_hash.toByteArray()
                                        )
                                    ).dataBagOfCells().roots.first().parse { Block.TlbCombinator.loadTlb(this) }
                                        .let { workchain to it }
                                }
                            }
                            .mergeWith(mono { -1 to it })
                    }
                    .flatMap {
                        val (workchain, block) = it
                        block.extra.account_blocks.nodes()
                            .flatMap {
                                sequenceOf(AddrStd(workchain, it.first.account_addr))
                                    .plus(it.first.transactions.nodes().map {
                                        AddrStd(workchain, it.first.account_addr)
                                    })
                            }
                            .toFlux()
                    }
                    .filter {// Exclude system addresses
                        it.address != BitString.of("5555555555555555555555555555555555555555555555555555555555555555") &&
                                it.address != BitString.of("3333333333333333333333333333333333333333333333333333333333333333") &&
                                it.address != BitString.of("0000000000000000000000000000000000000000000000000000000000000000")
                    }
                    .doOnNext {
                        logger.debug("affected account {}", value("address", it.toSafeBounceable()))
                    }
                    .replay()

            // Items
            affectedAccounts
                .flatMap { itemRepository.findById(it) } // Returns empty mono if not found, also acts as a filter
                .publishOn(Schedulers.boundedElastic())
                .doOnNext {
                    logger.info("address {} matched a database item", value("address", it.address.toSafeBounceable()))
                }
                .concatMap(itemProcess()) // Data and metadata
                .doOnNext { itemRepository.upsert(it).subscribe() }
                .doOnNext { // Royalty
                    if (it.collection != null)
                        it.address.toMono()
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(royaltyProcess())
                            .onErrorStop()
                            .subscribe { royaltyRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .doOnNext { // Sale
                    it.owner.toMono()
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(saleProcess())
                        .onErrorStop()
                        .subscribe { saleRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .then()
                .subscribe()

            // Collections
            affectedAccounts
                .publishOn(Schedulers.boundedElastic())
                .flatMap { collectionRepository.findById(it) } // Returns empty mono if not found, also acts as a filter
                .doOnNext {
                    logger.info(
                        "address {} matched a database collection",
                        value("address", it.address.toSafeBounceable())
                    )
                }
                .concatMap(collectionProcess()) // Data and metadata
                .doOnNext { collectionRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                .doOnNext { // Royalty
                    it.address.toMono()
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(royaltyProcess())
                        .onErrorStop()
                        .subscribe { royaltyRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe() }
                }
                .concatMap(missingItemsProcess())
                .doOnNext { itemRepository.save(it).subscribeOn(Schedulers.single()).subscribe() }
                .then()
                .subscribe()

            // Sales
            affectedAccounts
                .publishOn(Schedulers.boundedElastic())
                .flatMap { saleRepository.findById(it) } // Returns empty mono if not found, also acts as a filter
                .doOnNext {
                    logger.info("address {} matched a database sale", value("address", it.address.toSafeBounceable()))
                }
                .map { it.address }
                .flatMap(saleProcess())
                .doOnError {// Failed to get info for this address, remove it from the db
                    (Exceptions.unwrap(it) as? ProcessException)?.let {
                        logger.warn(
                            "couldn't get {} sale information, contract was probably destroyed. Removing from the db",
                            value("address", it.id.toSafeBounceable())
                        )
                        saleRepository.deleteById((Exceptions.unwrap(it) as ProcessException).id)
                            .subscribeOn(Schedulers.single()).subscribe()
                    }
                }
                .doOnNext {
                    saleRepository.upsert(it).subscribeOn(Schedulers.single()).subscribe()
                }
                .then()
                .subscribe()

            affectedAccounts.connect()
        }
    }

    companion object : KLogging()
}

fun <X> BinTree<X>.nodes(): Sequence<X> {
    return when (this) {
        is BinTreeLeaf -> sequenceOf(leaf)
        is BinTreeFork -> (left.nodes() + right.nodes())
    }
}
