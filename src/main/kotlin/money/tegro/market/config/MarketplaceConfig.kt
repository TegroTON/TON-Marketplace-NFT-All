package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import org.ton.block.AddrStd

@ConfigurationProperties("market.marketplace")
interface MarketplaceConfig {
    /** Address of market's main contract */
    @get:Bindable(defaultValue = "kQBzkX3JYsluRVlcwD9kaUPpBil0hVrKuIQ4OTOEfkD6tesA")
    val marketplaceAddress: AddrStd

    /** TODO: ACHTUNG This is the private key that is used to authorize operations of marketplace contract
     * While it doesn't directly allow anyone to control it, it will let potential attacker to sell items on the marketplace
     * without paying any fees or royalties. Be cautious!
     */
    @get:Bindable(defaultValue = "l9lQTQgJuJvB/10cIPS7M+ghy9JOz2VStA7PCm0lfIA=")
    val marketplaceAuthorizationPrivateKey: String

    /** What percentage from each sale goes to the market, numerator part of the fraction */
    @get:Bindable(defaultValue = "5")
    val feeNumerator: Long

    /** What percentage from each sale goes to the market, denominator part of the fraction */
    @get:Bindable(defaultValue = "100")
    val feeDenominator: Long

    /** Worst-case blockchain fees */
    @get:Bindable(defaultValue = "50_000_000")
    val blockchainFee: Long

    /** Fixed amount taken for putting an item up for sale, includes all processing fees */
    @get:Bindable(defaultValue = "150_000_000")
    val saleInitializationFee: Long

    /** Amount that is required to transfer an item from one account to another. Only used in /item/{}/transfer,
     * half of the sum is forwarded to the new owner
     */
    @get:Bindable(defaultValue = "100_000_000")
    val itemTransferAmount: Long
}
