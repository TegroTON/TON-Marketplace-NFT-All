package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLType
import money.tegro.market.contract.CollectionContract
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress

@GraphQLName("CollectionContract")
data class CollectionContractQuery(
    @GraphQLType("String")
    val size: BigInt,

    @GraphQLType("String")
    val owner: MsgAddress,
) {
    constructor(it: CollectionContract) : this(
        size = it.nextItemIndex.toString().toBigInteger(), // TODO
        owner = it.owner,
    )
}
