package money.tegro.market.nightcrawler.job

import io.micronaut.context.ApplicationContext
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.process.*
import mu.KLogging
import org.ton.block.AddrStd
import org.ton.block.Block
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
                        it.extra.account_blocks.nodes()
                            .map {
                                AddrStd(0, it.first.account_addr)
                            }
                            .toFlux()
                    }
                    .replay()

            val items = affectedAccounts
                .flatMap { itemRepository.findById(it) }
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
