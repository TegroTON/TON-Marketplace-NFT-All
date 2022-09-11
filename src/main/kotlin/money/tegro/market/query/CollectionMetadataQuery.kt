package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.metadata.CollectionMetadata

@GraphQLName("CollectionMetadata")
data class CollectionMetadataQuery(
    val name: String?,
    val description: String?,
    val image: String?,
    val coverImage: String?,
) {
    constructor(it: CollectionMetadata) : this(
        name = it.name,
        description = it.description,
        image = it.image,
        coverImage = it.coverImage,
    )
}
