package money.tegro.market

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
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

fun Cell.toBase64() = base64(BagOfCells(this).toByteArray())

fun Block.accountBlockAddresses() =
    this.extra.account_blocks.toMap()
        .keys
        .map { AddrStd(this.info.shard.workchain_id, it.account_addr) }

fun <T> Flow<T>.dropTake(drop: Int?, take: Int?): Flow<T> =
    this.drop(minOf(maxOf(drop ?: 0, 0), FLOW_DROPTAKE_DROP_MAX))
        .take(minOf(maxOf(take ?: FLOW_DROPTAKE_TAKE_MAX, 0), FLOW_DROPTAKE_DROP_MAX))

const val FLOW_DROPTAKE_DROP_MAX = 100
const val FLOW_DROPTAKE_TAKE_MAX = 100
