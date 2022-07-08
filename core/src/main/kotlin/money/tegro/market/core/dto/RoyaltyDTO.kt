package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Royalty", description = "Royalty information related to a specific item/collection")
data class RoyaltyDTO(
    @get:Schema(description = "Royalty value: 0 - no royalty, 0.15 - royalty of 15%")
    val value: Float,

    @get:Schema(description = "Address, to which royalty will be sent. Always base64url, bounceable")
    val destination: String?,
)
