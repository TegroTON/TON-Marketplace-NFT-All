package money.tegro.market.dto

import money.tegro.market.toRaw
import org.ton.block.MsgAddressInt

data class ShortItemDTO(
    val index: ULong,
    val address: String,
) {
    constructor(index: ULong, address: MsgAddressInt) : this(index, address.toRaw())
}
