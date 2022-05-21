package money.tegro.market.nft_tool

import org.ton.block.MsgAddressInt
import org.ton.cell.CellSlice

fun toAddress(slice: CellSlice): MsgAddressInt.AddrStd? {
    slice.loadBits(3) // addr_std: 10 + 0 for no anycast
    try { // TODO: properly check for end of slice
        return MsgAddressInt.AddrStd(
            null,
            slice.loadInt(8).toInt(),
            slice.loadBitString(256).toByteArray()
        )
    } catch (e: Exception) {
        return null
    }
}