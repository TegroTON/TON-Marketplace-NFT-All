package money.tegro.market.nightcrawler.worker

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTException
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.repository.RoyaltyRepository
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.accounts
import money.tegro.market.nightcrawler.WorkSinks.royalties
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions
import java.time.Duration
import java.time.Instant

@Singleton
class RoyaltyWorker(
    private var liteApi: LiteApi,
    private var configuration: NightcrawlerConfiguration,
    private var royaltyRepository: RoyaltyRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "setting up royalty worker" }

        royalties
            .asFlux()
            .concatMap(::processRoyalty)
            .subscribe()
    }

    private fun processRoyalty(address: AddrStd) = mono {
        royaltyRepository.findById(address).awaitSingleOrNull()?.let { dbRoyalty ->
            if (Duration.between(dbRoyalty.updated, Instant.now()) > configuration.royaltyUpdatePeriod) {
                logger.debug(
                    "updating existing royalty {} blockchain data",
                    StructuredArguments.value("address", dbRoyalty.address)
                )

                try {
                    val royalty = NFTRoyalty.of(dbRoyalty.address, liteApi)
                    var new = dbRoyalty.copy(
                        numerator = royalty.numerator,
                        denominator = royalty.denominator,
                        destination = royalty.destination,
                        updated = Instant.now(),
                    )

                    // Trigger other jobs
                    (dbRoyalty.destination as? AddrStd)?.let { accounts.tryEmitNext(it) }
                    if (dbRoyalty.destination != new.destination) // In case destination was changed, update both
                        (new.destination as? AddrStd)?.let { accounts.tryEmitNext(it) }

                    royaltyRepository.update(new).awaitSingleOrNull()
                } catch (e: NFTException) {
                    logger.error(
                        "WHAT THE FUCKING FUCK - couldn't get royalty for {} but record is in the database. Something's fishy",
                        StructuredArguments.value("address", dbRoyalty.address),
                        e
                    )
                    Exceptions.propagate(e)
                    null
                }
            } else {
                logger.debug(
                    "royalty {} blockchain data is up-to-date, last updated {}",
                    StructuredArguments.value("address", dbRoyalty.address),
                    StructuredArguments.value("updated", dbRoyalty.updated)
                )
                dbRoyalty
            }
        } ?: run {
            try {
                val royalty = NFTRoyalty.of(address, liteApi)

                logger.debug("saving new royalty {}", StructuredArguments.value("address", address))

                val new = RoyaltyModel(
                    address = royalty.address as AddrStd,
                    numerator = royalty.numerator,
                    denominator = royalty.denominator,
                    destination = royalty.destination,
                )
                royaltyRepository.save(new).awaitSingleOrNull()
            } catch (e: NFTException) {
                logger.info(
                    "contract {} doesn't implement royalty extension",
                    StructuredArguments.value("address", address), e
                )
                Exceptions.propagate(e)
                null
            }
        }
    }

    companion object : KLogging()
}
