package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLType
import money.tegro.market.contract.RoyaltyContract
import org.ton.block.MsgAddress

@GraphQLName("Royalty")
data class RoyaltyQuery(
    val value: Double,

    @GraphQLType("String")
    val destination: MsgAddress,
) {
    constructor(it: RoyaltyContract) : this(
        value = it.value(),
        destination = it.destination,
    )
}
