package money.tegro.market.data

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.metadata.CollectionMetadata

@GraphQLName("CollectionMetadata")
data class CollectionMetadataData(
    val name: String?,
    val description: String?,
    val image: String?,
) {
    constructor(it: CollectionMetadata) : this(
        name = it.name,
        description = it.description,
        image = it.image,
    )
}
