package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Collection", description = "Information about an NFT collection")
data class CollectionDTO(
    @get:Schema(description = "Address of the collection, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @get:Schema(description = "Number of items in the collection, indexes are zero-based and not guaranteed to be sequential (we are skipping uninitialized items)")
    val size: Long,

    @get:Schema(description = "Address of the collection owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @get:Schema(description = "Collection name")
    val name: String?,

    @get:Schema(description = "Collection description")
    val description: String?,

    @field:Schema(description = "Collection royalty parameters")
    val royalty: RoyaltyDTO?,
)
