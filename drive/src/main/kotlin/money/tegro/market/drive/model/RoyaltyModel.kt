package money.tegro.market.drive

import money.tegro.market.db.RoyaltyEntity
import org.ton.block.MsgAddressIntStd

data class RoyaltyModel(
    val value: Float,
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
