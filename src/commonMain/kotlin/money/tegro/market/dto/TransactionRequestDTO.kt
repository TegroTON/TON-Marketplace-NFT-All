package money.tegro.market.dto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.biginteger.BigIntegerHumanReadableSerializer
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequestDTO(
    val dest: String,
    @Serializable(with = BigIntegerHumanReadableSerializer::class)
    val value: BigInteger,
    val stateInit: String?,
    val text: String?,
    val payload: String?,
)
