package money.tegro.market.service

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.SaleContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.AccountKind
import money.tegro.market.model.AccountModel
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.AccountRepository
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient

@Singleton
open class ItemOwnerService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val accountRepository: AccountRepository,
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : ApplicationEventListener<StartupEvent> {
    @Async
    open override fun onApplicationEvent(event: StartupEvent?) {
        runBlocking(Dispatchers.Default) {
            merge(
                // Watch live
                liveAccounts
                    .filter { itemRepository.existsByOwner(it) || collectionRepository.existsByOwner(it) }
                    .onEach {
                        logger.info("{} matched database entity", kv("address", it.toSafeBounceable()))
                    },
                // Apart from watching live interactions, update them periodically
                flow {
                    while (currentCoroutineContext().isActive) {
                        logger.debug("running scheduled update of all database entities")
                        itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                            .mapNotNull { it.owner as? AddrStd }
                            .let { emitAll(it) }
                        delay(config.itemPeriod)
                    }
                }
            )
                // Work only on things that don't have an account entry yet
                .filter { !accountRepository.existsById(it) }
                .mapNotNull {
                    logger.debug("querying possible sales contract {}", kv("address", it.toSafeBounceable()))
                    try {
                        val data = SaleContract.of(it, liteClient)
                        AccountModel(
                            address = it,
                            kind = AccountKind.SALE,
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
                        AccountModel(it, AccountKind.USER) // Save it as a user account
                    }
                }
                .collect { accountRepository.save(it) }
        }
    }

    companion object : KLogging()
}
