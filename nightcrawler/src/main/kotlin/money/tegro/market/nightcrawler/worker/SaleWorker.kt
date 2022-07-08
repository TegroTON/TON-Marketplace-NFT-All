package money.tegro.market.nightcrawler.worker

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTException
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.repository.SaleRepository
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.accounts
import money.tegro.market.nightcrawler.WorkSinks.emitNextAccount
import money.tegro.market.nightcrawler.WorkSinks.emitNextItem
import money.tegro.market.nightcrawler.WorkSinks.sales
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions
import java.time.Duration
import java.time.Instant

@Singleton
class SaleWorker(
    private var liteApi: LiteApi,
    private var configuration: NightcrawlerConfiguration,
    private var saleRepository: SaleRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "setting up sale worker" }

        sales
            .asFlux()
            .concatMap(::processSale)
            .subscribe()
    }

    private fun processSale(address: AddrStd) = mono {
        saleRepository.findById(address).awaitSingleOrNull()?.let { dbSale ->
            if (Duration.between(dbSale.updated, Instant.now()) > configuration.saleUpdatePeriod) {
                logger.debug("updating existing sale {} blockchain data", value("address", dbSale.address))

                try {
                    val sale = NFTSale.of(dbSale.address, liteApi)
                    val new = dbSale.copy(
                        marketplace = sale.marketplace,
                        item = sale.item,
                        owner = sale.owner,
                        fullPrice = sale.fullPrice,
                        marketplaceFee = sale.marketplaceFee,
                        royalty = sale.royalty,
                        royaltyDestination = sale.royaltyDestination,
                        updated = Instant.now(),
                    )

                    // Trigger other jobs
                    (dbSale.item as? AddrStd)?.let { emitNextItem(it) }
                    (dbSale.owner as? AddrStd)?.let { emitNextAccount(it) }
                    if (dbSale.owner != new.owner) // In case owner was changed, update both
                        (new.owner as? AddrStd)?.let { emitNextAccount(it) }
                    (dbSale.royaltyDestination as? AddrStd)?.let { emitNextAccount(it) }
                    if (dbSale.royaltyDestination != new.royaltyDestination) // In case destination was changed, update both
                        (new.royaltyDestination as? AddrStd)?.let { emitNextAccount(it) }

                    saleRepository.update(new).awaitSingleOrNull()
                } catch (e: NFTException) {
                    logger.info(
                        "couldn't get sale {} information, item was most likely sold",
                        value("address", dbSale.address),
                        e
                    )
                    Exceptions.propagate(e)
                    null
                }
            } else {
                logger.debug(
                    "sale {} blockchain data is up-to-date, last updated {}",
                    value("address", dbSale.address),
                    value("updated", dbSale.updated)
                )
                dbSale
            }
        } ?: run {
            try {
                val sale = NFTSale.of(address, liteApi)

                logger.debug("saving new sale {}", value("address", address))

                val new = SaleModel(
                    address = sale.address as AddrStd,
                    marketplace = sale.marketplace,
                    item = sale.item,
                    owner = sale.owner,
                    fullPrice = sale.fullPrice,
                    marketplaceFee = sale.marketplaceFee,
                    royalty = sale.royalty,
                    royaltyDestination = sale.royaltyDestination,
                )

                (new.item as? AddrStd)?.let { emitNextItem(it) }
                (new.owner as? AddrStd)?.let { emitNextAccount(it) }
                (new.royaltyDestination as? AddrStd)?.let { emitNextAccount(it) }

                saleRepository.save(new).awaitSingleOrNull()
            } catch (e: NFTException) {
                logger.info(
                    "contract {} doesn't implement sales. Must be a regular user account",
                    value("address", address),
                    e
                )
                accounts.tryEmitNext(address)
                Exceptions.propagate(e)
                null
            }
        }
    }

    companion object : KLogging()
}
