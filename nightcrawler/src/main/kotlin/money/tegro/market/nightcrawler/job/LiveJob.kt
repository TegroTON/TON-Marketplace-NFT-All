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
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Singleton
class LiveJob(
    private val context: ApplicationContext,
    private val liteApi: LiteApi,

    private val itemRepository: ItemRepository,
    private val collectionRepository: CollectionRepository,
    private val saleRepository: SaleRepository,
    private val royaltyRepository: RoyaltyRepository,

    private val collectionDataProcess: CollectionDataProcess<LatestReferenceBlock>,
    private val collectionMetadataProcess: CollectionMetadataProcess,

    private val itemDataProcess: ItemDataProcess<LatestReferenceBlock>,
    private val itemMetadataProcess: ItemMetadataProcess,

    private val saleProcess: SaleProcess<LatestReferenceBlock>,
    private val royaltyProcess: RoyaltyProcess<LatestReferenceBlock>,
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

            val items = affectedAccounts
                .flatMap { itemRepository.findById(it) }
                .doOnNext { logger.info { "Updating item ${it.address.to().toSafeBounceable()}" } }
                .concatMap(itemDataProcess)
                .replay()

            // Update item sale
            items
                .concatMap {
                    it.owner?.let { saleProcess.apply(it) } ?: Mono.empty()
                }
                .subscribe { saleRepository.upsert(it) }

            // Update item metadata too
            items
                .concatMap(itemMetadataProcess)
                .subscribe { itemRepository.upsert(it) }

            // Update item royalty
            items
                .filter { it.collection != null } // only stand-alone items
                .map { it.address }
                .concatMap(royaltyProcess)
                .subscribe { royaltyRepository.upsert(it) }

            items.connect()


            val collections = affectedAccounts
                .flatMap { collectionRepository.findById(it) }
                .doOnNext { logger.info { "Updating collection ${it.address.to().toSafeBounceable()}" } }
                .replay()

            // Collection data+metadata
            collections
                .concatMap(collectionDataProcess)
                .concatMap(collectionMetadataProcess)
                .subscribe { collectionRepository.upsert(it) }

            // Update collection royalty
            collections
                .map { it.address }
                .concatMap(royaltyProcess)
                .subscribe { royaltyRepository.upsert(it) }

            collections.connect()

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
