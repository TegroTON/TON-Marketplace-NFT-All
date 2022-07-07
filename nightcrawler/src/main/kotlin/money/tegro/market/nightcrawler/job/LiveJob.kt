package money.tegro.market.nightcrawler.job

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.repository.*
import money.tegro.market.nightcrawler.Workers
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.keyValue
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Singleton
class LiveJob(
    private val liteApi: LiteApi,

    private val accountRepository: AccountRepository,
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val royaltyRepository: RoyaltyRepository,
    private val saleRepository: SaleRepository,

    private val workers: Workers
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "Remember, use your zoom, steady hands." }

            workers.run()

            val affectedAccounts =
                Flux.interval(Duration.ofSeconds(2))
                    .concatMap { mono { liteApi.getMasterchainInfo().last } }
                    .distinctUntilChanged()
                    .doOnNext { workers.referenceBlock = { it } }
                    .concatMap {
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
                    .concatMap {
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
                            .concatMap {
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
                    .concatMap {
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
                        logger.debug("affected account {}", value("address", it))
                    }
                    .doOnNext {
                        accountRepository.existsById(it)
                            .filter { it }
                            .subscribe { _ ->
                                logger.info("address {} matched database account entity", value("address", it))
                                workers.accounts.tryEmitNext(it)
                            }
                    }
                    .doOnNext {
                        collectionRepository.existsById(it)
                            .filter { it }
                            .subscribe { _ ->
                                logger.info("address {} matched database collection entity", value("address", it))
                                workers.collections.tryEmitNext(it)
                            }
                    }
                    .doOnNext {
                        itemRepository.existsById(it)
                            .filter { it }
                            .subscribe { _ ->
                                logger.info("address {} matched database item entity", value("address", it))
                                workers.items.tryEmitNext(it)
                            }
                    }
                    .doOnNext {
                        royaltyRepository.existsById(it)
                            .filter { it }
                            .subscribe { _ ->
                                logger.info("address {} matched database royalty entity", value("address", it))
                                workers.royalties.tryEmitNext(it)
                            }
                    }
                    .doOnNext {
                        saleRepository.existsById(it)
                            .filter { it }
                            .subscribe { _ ->
                                logger.info("address {} matched database sale entity", value("address", it))
                                workers.sales.tryEmitNext(it)
                            }
                    }
                    .subscribe()
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
