package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.model.destinationStd

@Schema(name = "Royalty", description = "Royalty information related to a specific item/collection")
data class RoyaltyDTO(
    @Schema(description = "Royalty value: 0 - no royalty, 0.15 - royalty of 15%")
    val value: Float,

    @Schema(description = "Address, to which royalty will be sent. Always base64url, bounceable")
    val destination: String?,
) {
    companion object {
        @JvmStatic
        fun of(it: RoyaltyModel) = it.numerator?.let { n ->
            it.denominator?.let { d ->
                it.destinationStd()?.let { dest -> RoyaltyDTO(n.toFloat() / d, dest.toSafeBounceable()) }
            }
        }
    }
}
