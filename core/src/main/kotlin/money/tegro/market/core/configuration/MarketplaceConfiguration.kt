package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import org.ton.block.AddrStd

@ConfigurationProperties("money.tegro.market.marketplace")
class MarketplaceConfiguration {
    /** Address of market's main contract */
    var marketplaceAddress = AddrStd("kQDZoy5eJAqm9cZoFnHzuoUDeMPUE_KETDsq7jeWnR20GwGK")

    /** What percentage from each sale goes to the market, numerator part of the fraction */
    var feeNumerator = 5

    /** What percentage from each sale goes to the market, denominator part of the fraction */
    var feeDenominator = 100

    /** Worst-case blockchain fees */
    var blockchainFee = 50_000_000L

    /** Fixed amount taken for putting an item up for sale, includes all processing fees */
    var saleInitializationFee = 150_000_000L
}
