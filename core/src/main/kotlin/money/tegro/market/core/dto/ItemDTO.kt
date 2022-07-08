package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema

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

    @field:Schema(description = "Item attributes")
    val attributes: Map<String, String>,

    @field:Schema(description = "Sale information if the item is on sale")
    val sale: SaleDTO?,

    @field:Schema(description = "Item royalty parameters, may be inherited from the collection or set up by the item itself")
    val royalty: RoyaltyDTO?,
)
