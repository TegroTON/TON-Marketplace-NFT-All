package money.tegro.market.drive.model

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.db.RoyaltyEntity
import org.ton.block.MsgAddressIntStd

data class RoyaltyModel(
    @Schema(description = "Royalty value: 0 - no royalty, 0.15 - royalty of 15%")
    val value: Float,

    @Schema(description = "Address, to which royalty will be sent. Always base64url, bounceable")
    val destination: String?,
) {
    companion object {
        fun of(it: RoyaltyEntity) = it.numerator?.let { n ->
            it.denominator?.let { d ->
                it.destinationWorkchain?.let { wc ->
                    it.destinationAddress?.let { addr ->
                        RoyaltyModel(
                            n.toFloat() / d,
                            MsgAddressIntStd(wc, addr).toGoodString()
                        )
                    }
                }
            }
        }
    }
}
