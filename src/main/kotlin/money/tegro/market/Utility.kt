package money.tegro.market

import org.ton.block.AddrStd
import org.ton.block.AddrVar
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

fun MsgAddressInt.toRaw() = when (this) {
    is AddrStd -> "$workchain_id:$address"
    is AddrVar -> "$workchain_id:$address"
}.lowercase()

fun MsgAddress.toRaw() = when (this) {
    is MsgAddressInt -> this.toRaw()
    else -> null
}
