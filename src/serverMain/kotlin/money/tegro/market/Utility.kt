package money.tegro.market

import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.crypto.base64

fun MsgAddressInt.toRaw() = when (this) {
    is AddrStd -> "$workchain_id:$address"
    is AddrVar -> "$workchain_id:$address"
}.lowercase()

fun MsgAddress.toRaw() = when (this) {
    is MsgAddressInt -> this.toRaw()
    else -> null
}

fun MsgAddress.toShortFriendly() = when (this) {
    is AddrStd -> this.toString(userFriendly = true, urlSafe = true, bounceable = true).let {
        it.take(4) + "..." + it.takeLast(5)
    }

    else -> null
}

fun Cell.toBase64() = base64(BagOfCells(this).toByteArray())

fun Block.accountBlockAddresses() =
    this.extra.account_blocks.toMap()
        .keys
        .map { AddrStd(this.info.shard.workchain_id, it.account_addr) }
