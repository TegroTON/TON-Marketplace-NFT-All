package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.configuration.MarketplaceConfiguration
import money.tegro.market.core.dto.SaleDTO
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.toSafeBounceable
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value

@Singleton
class SaleMapper(
    private val configuration: MarketplaceConfiguration,
) {
    fun map(it: SaleModel) = mono {
        // We store compatible sale contracts across marketplaces but only care about our own
        // Mainly because we can't be sure that third-party contracts are actually compatible, there's no standard
        if (it.marketplace == configuration.marketplaceAddress) {
            // Hideous stairway, needed to be absolutely sure that no malformed sales pass through
            // We can deal with this shit in the database, but it must not leak over to the client
            it.marketplace.toSafeBounceable()?.let { marketplace ->
                it.item.toSafeBounceable()?.let { item ->
                    it.owner.toSafeBounceable()?.let { owner ->
                        logger.debug(
                            "mapping sale {} of own marketplace {}",
                            value("address", it.address.toSafeBounceable()),
                            value("marketplace", marketplace)
                        )
                        SaleDTO(
                            address = it.address.toSafeBounceable(),
                            marketplace = marketplace,
                            item = item,
                            owner = owner,
                            fullPrice = it.fullPrice,
                            marketplaceFee = it.marketplaceFee,
                            royalty = it.royalty,
                            royaltyDestination = it.royaltyDestination.toSafeBounceable(),
                            // even if something went wrong while getting this, and it turns out to be null when not supposed to,
                            // royalty amount is already included in the fullPrice, so it's just a visual issue - no money lost, no user confused
                        )
                    }
                }
            }
        } else {
            logger.debug(
                "refusing to map sale {} of a foreign marketplace {}",
                value("address", it.address.toSafeBounceable()),
                value("marketplace", it.marketplace.toSafeBounceable())
            )
            null
        }
    }

    companion object : KLogging()
}
