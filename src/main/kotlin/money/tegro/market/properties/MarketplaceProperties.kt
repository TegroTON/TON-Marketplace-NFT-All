package money.tegro.market.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64

@ConfigurationProperties("market.marketplace")
class MarketplaceProperties {
    var marketplaceAddress: MsgAddressInt = MsgAddressInt("kQBzkX3JYsluRVlcwD9kaUPpBil0hVrKuIQ4OTOEfkD6tesA")

    var marketplaceSalePrivateKey: ByteArray = base64("l9lQTQgJuJvB/10cIPS7M+ghy9JOz2VStA7PCm0lfIA=")

    var feeNumerator: BigInt = BigInt(5)

    var feeDenominator: BigInt = BigInt(100)

    var saleInitializationFee: BigInt = BigInt(150_000_000)

    var networkFee: BigInt = BigInt(50_000_00)
}
