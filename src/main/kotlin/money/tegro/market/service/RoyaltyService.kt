package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.RoyaltyRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.time.Instant

@Singleton
open class RoyaltyService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val royaltyRepository: RoyaltyRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .concatMap { royaltyRepository.findById(it) }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.royaltyPeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { royaltyRepository.findAll(Sort.of(Sort.Order.asc("updated"))) }
        )
            .concatMap {
                mono {
                    logger.debug("updating royalty {}", kv("address", it.address.toSafeBounceable()))
                    try {
                        val data = RoyaltyContract.of(it.address, liteApi)
                        it.copy(
                            numerator = data.numerator,
                            denominator = data.denominator,
                            destination = data.destination,
                            updated = Instant.now(),
                        )
                    } catch (e: ContractException) {
                        logger.warn("could not get royalty for {}, removing entry", kv("address", it), e)
                        royaltyRepository.delete(it).subscribe()
                        null
                    }
                }
            }
            .subscribe { royaltyRepository.update(it).subscribe() }
    }

    companion object : KLogging()
}
