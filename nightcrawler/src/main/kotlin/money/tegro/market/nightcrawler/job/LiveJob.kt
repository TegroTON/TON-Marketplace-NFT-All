package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
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
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.lite.api.LiteApi
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
    //    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "Remember, use your zoom, steady hands." }

            val affectedAccounts =
                Flux.interval(Duration.ofSeconds(2))
                    .flatMap { mono { liteApi.getMasterchainInfo().last } }
                    .distinctUntilChanged()
                    .flatMap {
                        mono {
                            liteApi.getBlock(it).dataBagOfCells().roots.first().parse {
                                Block.TlbCombinator.loadTlb(this)
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
                        logger.info {
                            it.toString(
                                userFriendly = true,
                                urlSafe = true,
                                testOnly = true,
                                bounceable = true
                            )
                        }
                    }
                    .replay()

            affectedAccounts
                .publishOn(Schedulers.boundedElastic())
                .flatMap { itemRepository.findById(it) }
                .doOnNext { logger.info { "Updating Iitem ${it.address.toSafeBounceable()}" } }
                .concatMap(itemProcess()) // Data and metadata
                .doOnNext { itemRepository.upsert(it) }
                .doOnNext { // Royalty
                    if (it.collection != null)
                        it.address.toMono()
                            .flatMap(royaltyProcess())
                            .subscribe { royaltyRepository.upsert(it) }
                }
                .doOnNext { // Sale
                    it.address.toMono()
                        .flatMap(saleProcess())
                        .subscribe { saleRepository.upsert(it) }
                }
                .then()
                .subscribe {}


            affectedAccounts
                .publishOn(Schedulers.boundedElastic())
                .flatMap { collectionRepository.findById(it) }
                .doOnNext { logger.info { "Updating collection ${it.address.toSafeBounceable()}" } }
                .concatMap(collectionProcess()) // Data and metadata
                .doOnNext { collectionRepository.upsert(it) }
                .doOnNext { // Royalty
                    it.address.toMono()
                        .flatMap(royaltyProcess())
                        .subscribe { royaltyRepository.upsert(it) }
                }
                .concatMap(missingItemsProcess())
                .doOnNext { itemRepository.save(it) }
                .then()
                .subscribe {}

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
