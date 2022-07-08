package money.tegro.market.nightcrawler.job

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.repository.*
import money.tegro.market.nightcrawler.WorkSinks.emitNextAccount
import money.tegro.market.nightcrawler.WorkSinks.emitNextCollection
import money.tegro.market.nightcrawler.WorkSinks.emitNextItem
import money.tegro.market.nightcrawler.WorkSinks.emitNextRoyalty
import money.tegro.market.nightcrawler.WorkSinks.emitNextSale
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.keyValue
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.block.ShardDescr
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
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "Remember, use your zoom, steady hands." }

            Flux.interval(Duration.ofSeconds(2))
                .concatMap { mono { liteApi.getMasterchainInfo().last } }
                .distinctUntilChanged()
                .onBackpressureBuffer() // For the cases where blocks arrive faster than we are able to process
                .concatMap {
                    mono {
                        try {
                            logger.debug(
                                "getting masterchain block no. {}", value("seqno", it.seqno)
                            )
                            liteApi.getBlock(it).toBlock()
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
                        .toFlux()
                        .flatMapIterable {
                            val (key, value) = it
                            val workchain = BigInt(key.toByteArray()).toInt()
                            value.nodes().map { workchain to it }.asIterable()
                        }
                        .concatMap {
                            mono {
                                val (workchain, descr) = it
                                logger.debug(
                                    "getting shard {} block no. {}",
                                    keyValue("workchain", workchain),
                                    value("seqno", descr.seq_no)
                                )
                                liteApi.getBlock(getBlockId(workchain, descr)).toBlock()
                                    .let { workchain to it }
                            }
                        }
                        .mergeWith(mono { -1 to it }) // Don't forget the original masterchain block
                }
                .flatMapIterable {
                    val (workchain, block) = it
                    block.extra.account_blocks.nodes()
                        .flatMap {
                            sequenceOf(AddrStd(workchain, it.first.account_addr))
                                .plus(it.first.transactions.nodes().map {
                                    AddrStd(workchain, it.first.account_addr)
                                })
                        }
                        .asIterable()
                }
                .filter { it !in SYSTEM_ADDRESSES } // Exclude system addresses
                .doOnNext { logger.debug("affected account {}", value("address", it)) }
                .doOnNext {
                    accountRepository.existsById(it)
                        .filter { it }
                        .subscribe { _ ->
                            logger.info("address {} matched database account entity", value("address", it))
                            emitNextAccount(it)
                        }
                }
                .doOnNext {
                    collectionRepository.existsById(it)
                        .filter { it }
                        .subscribe { _ ->
                            logger.info("address {} matched database collection entity", value("address", it))
                            emitNextCollection(it)
                        }
                }
                .doOnNext {
                    itemRepository.existsById(it)
                        .filter { it }
                        .subscribe { _ ->
                            logger.info("address {} matched database item entity", value("address", it))
                            emitNextItem(it)
                        }
                }
                .doOnNext {
                    royaltyRepository.existsById(it)
                        .filter { it }
                        .subscribe { _ ->
                            logger.info("address {} matched database royalty entity", value("address", it))
                            emitNextRoyalty(it)
                        }
                }
                .doOnNext {
                    saleRepository.existsById(it)
                        .filter { it }
                        .subscribe { _ ->
                            logger.info("address {} matched database sale entity", value("address", it))
                            emitNextSale(it)
                        }
                }
                .subscribe()
        }
    }

    companion object : KLogging() {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )

        @JvmStatic
        fun getBlockId(workchain: Int, descr: ShardDescr) = TonNodeBlockIdExt(
            workchain = workchain,
            shard = descr.next_validator_shard,
            seqno = descr.seq_no.toInt(),
            root_hash = descr.root_hash,
            file_hash = descr.file_hash
        )
    }
}
