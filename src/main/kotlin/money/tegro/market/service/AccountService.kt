package money.tegro.market.service

import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.time.delay
import money.tegro.market.contract.ContractException
import money.tegro.market.contract.SaleContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.AccountKind
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.AccountRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient
import java.time.Instant

@Singleton
class AccountService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val accountRepository: AccountRepository,
) {
    @Scheduled(initialDelay = "0s")
    suspend fun setup() {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { accountRepository.findById(it) }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            flow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    emitAll(accountRepository.findAll(Sort.of(Sort.Order.asc("updated"))))
                    delay(config.accountPeriod)
                }
            }
        )
            .mapNotNull {
                when (it.kind) {
                    AccountKind.USER -> {
                        it.copy(updated = Instant.now())
                    }
                    AccountKind.SALE -> {
                        logger.debug("updating sale {}", kv("address", it.address.toSafeBounceable()))
                        try {
                            val data = SaleContract.of(it.address, liteClient)
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
                            accountRepository.delete(it)
                            null
                        }
                    }
                }
            }
            .collect { accountRepository.update(it) }
    }

    companion object : KLogging()
}
