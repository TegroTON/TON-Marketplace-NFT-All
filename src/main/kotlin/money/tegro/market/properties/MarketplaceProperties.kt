package money.tegro.market.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64

@ConfigurationProperties("market.marketplace")
class MarketplaceProperties {
    var marketplaceAddress: MsgAddressInt = MsgAddressInt("EQDvuJSj-Ay8zz_fgHOoLUm0AaAJul7pihPgtCkTeU07djgA")

    var marketplaceAuthorizationPrivateKey: ByteArray = base64("+Oag5l7EVtrqAPlV3CMGUxWNr4uMn5p6h5eWGwK9xcc=")

    var marketplaceFeeNumerator = 5

    var marketplaceFeeDenominator = 100

    var saleInitializationFee: BigInt = BigInt(100_000_000)

    var itemTransferFee: BigInt = BigInt(50_000_000)

    var networkFee: BigInt = BigInt(50_000_000)
}
