package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import org.ton.block.MsgAddressIntStd

@ConfigurationProperties("money.tegro.market.marketplace")
class MarketplaceConfiguration {
    /** Address of market's main contract */
    var marketplaceAddress = MsgAddressIntStd("kQBBCW5MYUEjNUCemgZaHF8ZHf1lbjq0k8qBonydAlfXz5a9")

    /** What percentage from each sale goes to the market, numerator part of the fraction */
    var feeNumerator = 5

    /** What percentage from each sale goes to the market, denominator part of the fraction */
    var feeDenominator = 100

    /** Worst-case blockchain fees */
    var blockchainFee = 50_000_000L

    /** Fixed amount taken for putting an item up for sale, includes all processing fees */
    var saleInitializationFee = 100_000_000L
}
