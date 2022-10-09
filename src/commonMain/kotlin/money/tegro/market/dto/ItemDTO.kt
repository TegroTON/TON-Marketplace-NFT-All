package money.tegro.market.dto

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.serialization.Contextual
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
    @Contextual
    val fullPrice: BigInteger?,
    @Contextual
    val marketplaceFee: BigInteger?,
    @Contextual
    val royalties: BigInteger?,
    val royaltyDestination: String?,
    @Contextual
    val royaltyPercentage: BigDecimal,
    @Contextual
    val marketplaceFeePercentage: BigDecimal,

    @Contextual
    val saleInitializationFee: BigInteger,
    @Contextual
    val transferFee: BigInteger,
    @Contextual
    val networkFee: BigInteger,
    @Contextual
    val minimalGasFee: BigInteger,
)
