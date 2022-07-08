package money.tegro.market.nightcrawler.worker

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.model.AccountModel
import money.tegro.market.core.repository.AccountRepository
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.accounts
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import java.time.Duration
import java.time.Instant

@Singleton
class AccountWorker(
    private var liteApi: LiteApi,
    private var configuration: NightcrawlerConfiguration,
    private var accountRepository: AccountRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "setting up account worker" }

        accounts
            .asFlux()
            .concatMap(::processAccount)
            .subscribe()
    }

    private fun processAccount(address: AddrStd) = mono {
        accountRepository.findById(address).awaitSingleOrNull()?.let { dbAccount ->
            if (Duration.between(dbAccount.updated, Instant.now()) > configuration.accountUpdatePeriod) {
                logger.debug(
                    "updating existing account {} blockchain data",
                    StructuredArguments.value("address", dbAccount.address)
                )

                val new = dbAccount.copy(updated = Instant.now())

                accountRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "account {} blockchain data is up-to-date, last updated {}",
                    StructuredArguments.value("address", dbAccount.address),
                    StructuredArguments.value("updated", dbAccount.updated)
                )
                dbAccount
            }
        } ?: run {
            logger.debug("saving new account {}", StructuredArguments.value("address", address))

            val new = AccountModel(
                address = address
            )

            accountRepository.save(new).awaitSingleOrNull()
        }
    }

    companion object : KLogging()
}
