package money.tegro.market.server.properties

import io.ktor.server.application.*
import mu.KLogging
import org.ton.bigint.BigInt
import org.ton.block.Coins
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64

data class MarketplaceProperties(
    val address: MsgAddressInt = MsgAddressInt("EQDvuJSj-Ay8zz_fgHOoLUm0AaAJul7pihPgtCkTeU07djgA"),

    val authorizationPrivateKey: ByteArray = base64("+Oag5l7EVtrqAPlV3CMGUxWNr4uMn5p6h5eWGwK9xcc="),

    val serviceFeeNumerator: Int = 5,

    val serviceFeeDenominator: Int = 100,

    val saleFee: Coins = Coins.ofNano(100_000_000),

    val transferFee: Coins = Coins.ofNano(50_000_000),

    val networkFee: Coins = Coins.ofNano(50_000_000),

    val gasFee: Coins = Coins.ofNano(1_000_000_000),
) {
    companion object : KLogging() {
        @JvmStatic
        fun fromEnvironment(environment: ApplicationEnvironment) =
            MarketplaceProperties(
                address = environment.config.propertyOrNull("marketplace.address")?.getString()
                    ?.let { MsgAddressInt(it) }
                    ?: MarketplaceProperties().address,
                authorizationPrivateKey = environment.config.propertyOrNull("marketplace.authorization")
                    ?.getString()
                    ?.let { base64(it) }
                    ?: MarketplaceProperties().authorizationPrivateKey,
                serviceFeeNumerator = environment.config.propertyOrNull("marketplace.fee.service.numerator")
                    ?.getString()
                    ?.toInt()
                    ?: MarketplaceProperties().serviceFeeNumerator,
                serviceFeeDenominator = environment.config.propertyOrNull("marketplace.fee.service.denominator")
                    ?.getString()
                    ?.toInt()
                    ?: MarketplaceProperties().serviceFeeDenominator,
                saleFee = environment.config.propertyOrNull("marketplace.fee.sale")?.getString()
                    ?.let { Coins.ofNano(BigInt(it)) }
                    ?: MarketplaceProperties().saleFee,
                transferFee = environment.config.propertyOrNull("marketplace.fee.transfer")?.getString()
                    ?.let { Coins.ofNano(BigInt(it)) }
                    ?: MarketplaceProperties().transferFee,
                networkFee = environment.config.propertyOrNull("marketplace.fee.network")?.getString()
                    ?.let { Coins.ofNano(BigInt(it)) }
                    ?: MarketplaceProperties().networkFee,
                gasFee = environment.config.propertyOrNull("marketplace.fee.gas")?.getString()
                    ?.let { Coins.ofNano(BigInt(it)) }
                    ?: MarketplaceProperties().gasFee,
            )
    }
}
