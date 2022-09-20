package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.metadata.ItemMetadataAttribute

@GraphQLName("ItemAttribute")
data class ItemAttributeQuery(
    val trait: String,
    val value: String,
) {
    constructor(it: ItemMetadataAttribute) : this(
        it.trait,
        it.value
    )
}
