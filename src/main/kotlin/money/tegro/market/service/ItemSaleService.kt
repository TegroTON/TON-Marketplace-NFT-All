package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.SaleContract
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.AccountModel
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.AccountRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.repository.SaleRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.extra.bool.logicalAnd
import reactor.kotlin.extra.bool.not
import java.time.Duration

@Singleton
open class ItemSaleService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,
    private val liveAccounts: Flux<AddrStd>,

    private val accountRepository: AccountRepository,
    private val itemRepository: ItemRepository,
    private val saleRepository: SaleRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        Flux.merge(
            // Watch live
            liveAccounts
                .filterWhen { itemRepository.existsByOwner(it) }
                .doOnNext {
                    logger.info("{} matched database entity", kv("address", it.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            Flux.interval(Duration.ZERO, config.salePeriod)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext { logger.debug("running scheduled update of all database entities") }
                .concatMap { itemRepository.findAll().concatMap { (it.owner as? AddrStd).toMono() } }
        )
            // Work only on things that don't have a sales/account entry yet
            .filterWhen { saleRepository.existsById(it).not().logicalAnd(accountRepository.existsById(it).not()) }
            .concatMap {
                mono {
                    logger.debug("querying possible sales contract {}", kv("address", it.toSafeBounceable()))
                    try {
                        val data = SaleContract.of(it, liteApi)
                        SaleModel(
                            address = it,
                            marketplace = data.marketplace,
                            item = data.item,
                            owner = data.owner,
                            fullPrice = data.fullPrice,
                            marketplaceFee = data.marketplaceFee,
                            royalty = data.royalty,
                            royaltyDestination = data.royaltyDestination,
                        )
                    } catch (e: ContractException) {
                        logger.trace("{} doesn't implement sales", kv("address", it.toSafeBounceable()), e)
                        accountRepository.save(AccountModel(it)).subscribe() // Save it as a user account
                        null // Just skip on error
                    }
                }
            }
            .subscribe { saleRepository.save(it).subscribe() }
    }

    companion object : KLogging()
}
