package money.tegro.market.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.ton.block.Coins
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64

@ConfigurationProperties("market.marketplace")
class MarketplaceProperties {
    var marketplaceAddress: MsgAddressInt = MsgAddressInt("EQDvuJSj-Ay8zz_fgHOoLUm0AaAJul7pihPgtCkTeU07djgA")

    var marketplaceAuthorizationPrivateKey: ByteArray = base64("+Oag5l7EVtrqAPlV3CMGUxWNr4uMn5p6h5eWGwK9xcc=")

    var marketplaceFeeNumerator = 5

    var marketplaceFeeDenominator = 100

    var saleInitializationFee: Coins = Coins.ofNano(100_000_000)

    var itemTransferFee: Coins = Coins.ofNano(50_000_000)

    var networkFee: Coins = Coins.ofNano(50_000_000)

    val minimalGasFee: Coins = Coins.ofNano(1_000_000_000)
}
