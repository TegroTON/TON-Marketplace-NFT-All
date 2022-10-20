package money.tegro.market.server

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.take
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

fun MsgAddressInt.toBlob() =
    ExposedBlob(BagOfCells(CellBuilder.createCell { storeTlb(MsgAddressInt, this@toBlob) }).toByteArray())

fun ExposedBlob.toMsgAddressInt() =
    BagOfCells(this.bytes).roots.first().parse { loadTlb(MsgAddressInt) }

fun BigInt.toBigInteger() =
    com.ionspin.kotlin.bignum.integer.BigInteger.parseString(this.toString()) // TODO

fun <T> Flow<T>.dropTake(drop: Int?, take: Int?): Flow<T> =
    this.drop(maxOf(drop ?: 0, 0))
        .take(minOf(maxOf(take ?: 16, 0), FLOW_DROPTAKE_TAKE_MAX))

const val FLOW_DROPTAKE_TAKE_MAX = 128
