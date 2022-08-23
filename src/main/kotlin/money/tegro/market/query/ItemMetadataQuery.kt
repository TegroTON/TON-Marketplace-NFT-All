package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.metadata.ItemMetadata

@GraphQLName("ItemMetadata")
data class ItemMetadataQuery(
    val name: String?,
    val description: String?,
    val image: String?,
) {
    constructor(it: ItemMetadata) : this(
        name = it.name,
        description = it.description,
        image = it.image,
    )
}
