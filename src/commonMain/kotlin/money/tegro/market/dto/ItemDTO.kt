package money.tegro.market.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.bigdecimal.BigDecimalHumanReadableSerializer
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerHumanReadableSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ItemDTO(
    val address: String,
    val index: ULong,
    val collection: String?,
    val owner: String?,
    val name: String,
    val description: String,
    val image: ImageDTO,
    val attributes: Map<String, String>,

    val sale: String?,
    val marketplace: String?,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val fullPrice: BigInteger?,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val marketplaceFee: BigInteger?,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val royalties: BigInteger?,
    val royaltyDestination: String?,
    @Serializable(with = BigDecimalHumanReadableSerializer::class)
    val royaltyPercentage: BigDecimal,
    @Serializable(with = BigDecimalHumanReadableSerializer::class)
    val marketplaceFeePercentage: BigDecimal,

    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val saleInitializationFee: BigInteger,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val transferFee: BigInteger,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val networkFee: BigInteger,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val minimalGasFee: BigInteger,
)
