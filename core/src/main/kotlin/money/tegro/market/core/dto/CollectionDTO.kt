package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.addressStd
import money.tegro.market.core.model.ownerStd

@Schema(name = "Collection", description = "Information about an NFT collection")
data class CollectionDTO(
    @Schema(description = "Address of the collection, uniquely identifies it. Always base64url, bounceable")
    val address: String,
    @Schema(description = "Number of items in the collection, indexes are zero-based and not guaranteed to be sequential (we are skipping uninitialized items)")
    val size: Long?,
    @Schema(description = "Address of the collection owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,
    @Schema(description = "Collection name")
    val name: String?,
    @Schema(description = "Collection description")
    val description: String?,
    @Schema(description = "Collection title image information")
    val image: String?,
    @Schema(description = "Collection cover image (banner) information")
    val coverImage: String?,
    @Schema(description = "Collection royalty parameters")
    val royalty: RoyaltyDTO?,
) {
    constructor(it: CollectionModel) : this(
        address = it.addressStd().toSafeBounceable(),
        size = it.nextItemIndex,
        owner = it.ownerStd()?.toSafeBounceable(),
        name = it.name,
        description = it.description,
        image = it.image,
        coverImage = it.coverImage,
        royalty = RoyaltyDTO.of(it)
    )
}
