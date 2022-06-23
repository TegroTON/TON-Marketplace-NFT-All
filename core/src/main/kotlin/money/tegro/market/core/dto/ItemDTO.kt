package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel

@Schema(name = "Item", description = "Information about an NFT item")
data class ItemDTO(
    @get:Schema(description = "NFT item address, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @get:Schema(description = "Index of the item in the collection, if in one")
    val index: Long?,

    @get:Schema(description = "NFT collection address, uniquely identifies it. Always base64url, bounceable")
    val collection: String?,

    @get:Schema(description = "Address of the item owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @get:Schema(description = "Item name")
    val name: String?,

    @get:Schema(description = "Description of the item")
    val description: String?,

    @get:Schema(description = "Item image information")
    val image: String?,

    @field:Schema(description = "Item attributes")
    val attributes: Map<String, String>?,

    @field:Schema(description = "Item royalty parameters, may be inherited from the collection or set up by the item itself")
    val royalty: RoyaltyDTO?,
) {
    constructor(it: ItemModel, collection: CollectionModel?, attributes: Iterable<AttributeModel>?) : this(
        address = it.address.to().toSafeBounceable(),
        index = it.index,
        collection = collection?.address?.to()?.toSafeBounceable(),
        owner = it.owner?.to()?.toSafeBounceable(),
        name = it.name,
        description = it.description,
        image = it.image,
        attributes = attributes?.map { it.trait to it.value }?.toMap() ?: mapOf(),
        royalty = collection?.let { RoyaltyDTO.of(it) } ?: RoyaltyDTO.of(it)
    )
}
