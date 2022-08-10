package money.tegro.market.model

import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.bytes
import org.ktorm.schema.decimal
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.parse
import org.ton.tlb.storeTlb
import java.math.BigInteger

fun BaseTable<*>.msgAddressInt(name: String): Column<MsgAddressInt> =
    bytes(name).transform(
        { BagOfCells(it).roots.first().parse(MsgAddressInt) },
        { BagOfCells(CellBuilder.createCell { storeTlb(MsgAddressInt, it) }).toByteArray() }
    )

fun BaseTable<*>.msgAddress(name: String): Column<MsgAddress> =
    bytes(name).transform(
        { BagOfCells(it).roots.first().parse(MsgAddress) },
        { BagOfCells(CellBuilder.createCell { storeTlb(MsgAddress, it) }).toByteArray() }
    )

fun BaseTable<*>.numeric(name: String): Column<BigInteger> =
    decimal(name).transform(
        { it.toBigIntegerExact() },
        { it.toBigDecimal() }
    )
