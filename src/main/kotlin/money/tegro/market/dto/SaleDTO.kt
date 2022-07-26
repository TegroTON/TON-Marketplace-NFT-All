package money.tegro.market.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Sale", description = "Sale information of a specific item")
data class SaleDTO(
    @get:Schema(description = "Sale contract address, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @get:Schema(description = "Marketplace contract address, null for foreign marketplaces. Always base64url, bounceable")
    val marketplace: String,

    @get:Schema(description = "Address of the item that is being sold, uniquely identifies it. Always base64url, bounceable")
    val item: String,

    @get:Schema(description = "Owner of this seller contract, an account that will receive coins on successful sale. Always base64url, bounceable")
    val owner: String,

    @get:Schema(description = "Full price, what another account has to pay (excl. fees) to receive an item. In nanotons")
    val fullPrice: Long,

    @get:Schema(description = "Amount subtracted from the full price that will be sent to the marketplace. In nanotons")
    val marketplaceFee: Long,

    @get:Schema(description = "Amount subtracted from the full price that will be sent to the royalty holder. In nanotons")
    val royalty: Long?,

    @get:Schema(description = "Royalty holder address, always base64url, bounceable")
    val royaltyDestination: String?,
)