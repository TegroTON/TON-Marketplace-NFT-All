package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.toSafeBounceable

@Schema(name = "Item", description = "Information about an NFT item")
data class ItemDTO(
    @get:Schema(description = "NFT item address, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @get:Schema(description = "Index of the item in the collection, if in one")
    val index: Long?,

    @get:Schema(description = "NFT collection address, uniquely identifies it. Always base64url, bounceable")
    val collection: String?,

    @get:Schema(description = "Address of the item owner or seller contract, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @get:Schema(description = "Item name")
    val name: String?,

    @get:Schema(description = "Description of the item")
    val description: String?,

    @field:Schema(description = "Sale information if the item is on sale")
    val sale: SaleDTO?,

    @field:Schema(description = "Item royalty parameters, may be inherited from the collection or set up by the item itself")
    val royalty: RoyaltyDTO?,

    @field:Schema(description = "Item attributes")
    val attributes: Map<String, String>?,
) {
    constructor(
        it: ItemModel,
        sale: SaleModel?,
        royalty: RoyaltyModel?,
        attributes: Iterable<AttributeModel>?,
    ) : this(
        address = it.address.toSafeBounceable(),
        index = it.index,
        collection = it.collection.toSafeBounceable(),
        owner = it.owner.toSafeBounceable(),
        name = it.name,
        description = it.description,
        sale = sale?.let { SaleDTO(it) },
        royalty = royalty?.let { RoyaltyDTO(it) },
        attributes = attributes?.associate { it.trait to it.value } ?: mapOf(),
    )
}
