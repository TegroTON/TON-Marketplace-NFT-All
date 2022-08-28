package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.contract.CollectionContract
import money.tegro.market.toRaw

@GraphQLName("CollectionContract")
data class CollectionContractQuery(
    val size: String,
    val owner: String?,
) {
    constructor(it: CollectionContract) : this(
        size = it.next_item_index.toString(),
        owner = it.owner.toRaw(),
    )
}
