package money.tegro.market.dto

import com.expediagroup.graphql.generator.annotations.GraphQLType
import money.tegro.market.toRaw
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt

data class ShortItemDTO(
    @GraphQLType("String")
    val index: BigInt,
    val address: String,
) {
    constructor(index: ULong, address: MsgAddressInt) : this(index.toString().toBigInteger(), address.toRaw())
}
