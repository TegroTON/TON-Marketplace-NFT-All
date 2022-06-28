package money.tegro.market.nightcrawler

import io.micronaut.context.ApplicationContext
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.SaleRepository
import mu.KLogging
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

@Singleton
class LiveJob(
    private val context: ApplicationContext,
    private val liteApi: LiteApi,

    private val itemRepository: ItemRepository,
    private val collectionRepository: CollectionRepository,
    private val saleRepository: SaleRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "Remember, use your zoom, steady hands." }

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
                .subscribe {
                    logger.info { it.toSafeBounceable() }
                    itemRepository.findById(it).subscribe {
                        logger.info { "Item hit! ${it.address.to().toSafeBounceable()}" }
                    }

                    collectionRepository.findById(it).subscribe {
                        logger.info { "Collection hit! ${it.address.to().toSafeBounceable()}" }
                    }

                    saleRepository.findById(it).subscribe {
                        logger.info { "Sale hit! ${it.address.to().toSafeBounceable()}" }
                    }
                }
        }
    }


    companion object : KLogging()
}
