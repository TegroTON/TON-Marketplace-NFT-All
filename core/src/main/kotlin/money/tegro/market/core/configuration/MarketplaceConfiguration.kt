package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import org.ton.block.AddrStd
import org.ton.crypto.base64

@ConfigurationProperties("money.tegro.market.marketplace")
class MarketplaceConfiguration {
    /** Address of market's main contract */
    var marketplaceAddress = AddrStd("kQBzkX3JYsluRVlcwD9kaUPpBil0hVrKuIQ4OTOEfkD6tesA")

    /** TODO: ACHTUNG This is the private key that is used to authorize operations of marketplace contract
     * While it doesn't directly allow anyone to control it, it will let potential attacker to sell items on the marketplace
     * without paying any fees or royalties. Be cautious!
     */
    var marketplaceAuthorizationPrivateKey = base64("l9lQTQgJuJvB/10cIPS7M+ghy9JOz2VStA7PCm0lfIA=")

    /** What percentage from each sale goes to the market, numerator part of the fraction */
    var feeNumerator = 5

    /** What percentage from each sale goes to the market, denominator part of the fraction */
    var feeDenominator = 100

    /** Worst-case blockchain fees */
    var blockchainFee = 50_000_000L

    /** Fixed amount taken for putting an item up for sale, includes all processing fees */
    var saleInitializationFee = 150_000_000L

    /** Amount that is required to transfer an item from one account to another. Only used in /item/{}/transfer,
     * half of the sum is forwarded to the new owner
     */
    var itemTransferAmount = 100_000_000L
}
