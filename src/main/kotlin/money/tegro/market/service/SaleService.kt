package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.SaleContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.SaleRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant

@Singleton
open class SaleService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val saleRepository: SaleRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .concatMap { saleRepository.findById(it) }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.salePeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { saleRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
        )
            .concatMap {
                mono {
                    logger.debug("updating sale {}", kv("address", it.address.toSafeBounceable()))
                    try {
                        val data = SaleContract.of(it.address, liteApi)
                        it.copy(
                            marketplace = data.marketplace,
                            item = data.item,
                            owner = data.owner,
                            fullPrice = data.fullPrice,
                            marketplaceFee = data.marketplaceFee,
                            royalty = data.royalty,
                            royaltyDestination = data.royaltyDestination,
                            updated = Instant.now(),
                        )
                    } catch (e: ContractException) {
                        logger.info("could not get sale {} info, removing entry", kv("address", it), e)
                        saleRepository.delete(it).subscribe()
                        null
                    }
                }
            }
            .subscribe { saleRepository.update(it).subscribe() }
    }

    companion object : KLogging()
}
