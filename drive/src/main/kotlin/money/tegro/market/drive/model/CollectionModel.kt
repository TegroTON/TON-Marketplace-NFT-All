package money.tegro.market.drive.model

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.db.CollectionInfo

data class CollectionModel(
    @Schema(description = "Address of the collection, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @Schema(description = "Number of items in the collection, indexes are zero-based and not guaranteed to be sequential (we are skipping uninitialized items)")
    val size: Long?,

    @Schema(description = "Address of the collection owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @Schema(description = "Royalty parameters of the collection")
    val royalty: RoyaltyModel?,

    @Schema(description = "Collection name")
    val name: String?,

    @Schema(description = "Collection description")
    val description: String?,

    @Schema(description = "Collection title image information")
    val image: ImageModel?,

    @Schema(description = "Collection cover image (banner) information")
    val coverImage: ImageModel?,
) {
    constructor(it: CollectionInfo) : this(
        address = it.addressStd().toGoodString(),
        size = it.nextItemIndex,
        owner = it.owner()?.toGoodString(),
        royalty = it.royalty?.let { royalty -> RoyaltyModel.of(royalty) },
        name = it.metadata?.name,
        description = it.metadata?.description,
        image = ImageModel("image", it.addressStd(), it.metadata?.image),
        coverImage = ImageModel("cover", it.addressStd(), it.metadata?.coverImage),
    )
}
