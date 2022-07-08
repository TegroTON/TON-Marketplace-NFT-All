package money.tegro.market.nightcrawler.job

import io.micronaut.context.annotation.Prototype
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.scheduling.annotation.Scheduled
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.repository.*
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.accounts
import money.tegro.market.nightcrawler.WorkSinks.collections
import money.tegro.market.nightcrawler.WorkSinks.items
import money.tegro.market.nightcrawler.WorkSinks.royalties
import money.tegro.market.nightcrawler.WorkSinks.sales
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import java.time.Duration

@Prototype
class CatchUpJob(
    private val configuration: NightcrawlerConfiguration,
    private val liteApi: LiteApi,
    private val resourceLoader: ClassPathResourceLoader,
    private val accountRepository: AccountRepository,
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val royaltyRepository: RoyaltyRepository,
    private val saleRepository: SaleRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        runBlocking {
            logger.info { "setting up catch-up job" }

            Flux.interval(Duration.ZERO, configuration.catchUpPeriod)
                .onBackpressureDrop {
                    logger.warn { "catch-up attempt already in progress, period must be too short" }
                }
                .doOnNext {
                    mono {
                        val startBlock = liteApi.getMasterchainInfo().last
                        logger.info("starting catching up at block no. {}", value("seqno", startBlock.seqno))
                    }.subscribe()
                }
                .doOnNext {
                    logger.info { "loading initial collections" }
                    val resource = resourceLoader.classLoader.getResource("init_collections.csv")

                    if (resource == null) {
                        logger.warn { "no file with initial collections was found in the classpath" }
                    } else {
                        resource.readText()
                            .lineSequence()
                            .filter { it.isNotBlank() }
                            .map { AddrStd(it) }
                            .forEach {
                                logger.debug("queueing initial collection {}", value("address", it))
                                collections.tryEmitNext(it)
                            }
                    }
                }
                .doOnNext {
                    logger.info { "loading database accounts" }
                    accountRepository.findAll().subscribe { accounts.tryEmitNext(it.address) }
                }
                .doOnNext {
                    logger.info { "loading database collections" }
                    collectionRepository.findAll().subscribe { collections.tryEmitNext(it.address) }
                }
                .doOnNext {
                    logger.info { "loading database items" }
                    itemRepository.findAll().subscribe { items.tryEmitNext(it.address) }
                }
                .doOnNext {
                    logger.info { "loading database royalties" }
                    royaltyRepository.findAll().subscribe { royalties.tryEmitNext(it.address) }
                }
                .doOnNext {
                    logger.info { "loading database sales" }
                    saleRepository.findAll().subscribe { sales.tryEmitNext(it.address) }
                }
                .subscribe()
        }
    }

    companion object : KLogging()
}
