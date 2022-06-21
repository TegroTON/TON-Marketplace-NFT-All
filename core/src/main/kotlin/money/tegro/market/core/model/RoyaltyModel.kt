package money.tegro.market.core.model

import org.ton.block.MsgAddressIntStd
import java.time.Instant

interface RoyaltyModel : BasicModel {
    var numerator: Int?
    var denominator: Int?
    var destinationWorkchain: Int?
    var destinationAddress: ByteArray?

    var royaltyUpdated: Instant?
    var royaltyModified: Instant?
}

fun RoyaltyModel.destinationStd() = destinationWorkchain?.let { wc ->
    destinationAddress?.let { addr ->
        MsgAddressIntStd(
            wc,
            addr
        )
    }
}
