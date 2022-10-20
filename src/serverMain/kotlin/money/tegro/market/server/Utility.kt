package money.tegro.market.server

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
