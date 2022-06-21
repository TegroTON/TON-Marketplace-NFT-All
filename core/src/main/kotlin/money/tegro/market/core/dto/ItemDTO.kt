package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.addressStd
import money.tegro.market.core.model.ownerStd

@Schema(name = "Item", description = "Information about an NFT item")
data class ItemDTO(
    @Schema(description = "NFT item address, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @Schema(description = "Index of the item in the collection, if in one")
    val index: Long?,

    @Schema(description = "NFT collection address, uniquely identifies it. Always base64url, bounceable")
    val collection: String?,

    @Schema(description = "Address of the item owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @Schema(description = "Item name")
    val name: String?,

    @Schema(description = "Description of the item")
    val description: String?,

    @Schema(description = "Item image information")
    val image: String?,

    @Schema(description = "Item royalty parameters, may be inherited from the collection or set up by the item itself")
    val royalty: RoyaltyDTO?,
) {
    constructor(it: ItemModel, collection: CollectionModel?) : this(
        address = it.addressStd().toSafeBounceable(),
        index = it.index,
        collection = collection?.addressStd()?.toSafeBounceable(),
        owner = it.ownerStd()?.toSafeBounceable(),
        name = it.name,
        description = it.description,
        image = it.image,
        royalty = collection?.let { RoyaltyDTO.of(it) } ?: RoyaltyDTO.of(it)
    )
}
