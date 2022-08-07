package money.tegro.market.core

import org.ton.block.AddrStd
import org.ton.block.AddrVar
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

fun AddrStd.toSafeBounceable() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)

fun MsgAddress.toSafeBounceable() = (this as? AddrStd)?.toSafeBounceable()

fun MsgAddressInt.toRaw() = when (this) {
    is AddrStd -> "$workchain_id:$address"
    is AddrVar -> "$workchain_id:$address"
}
