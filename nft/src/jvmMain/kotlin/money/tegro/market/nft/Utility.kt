package money.tegro.market.nft

import io.ipfs.multihash.Multihash
import org.ton.block.MsgAddressInt
import org.ton.cell.CellSlice

fun CellSlice.loadMsgAddr(): MsgAddressInt.AddrStd? {
    this.loadBits(3) // addr_std: 10 + 0 for no anycast
    try { // TODO: properly check for end of slice
        return MsgAddressInt.AddrStd(
            null,
            this.loadInt(8).toInt(),
            this.loadBitString(256).toByteArray()
        )
    } catch (e: Exception) {
        return null
    }
}

// dirty hack to make ipfs work
class DummyMultihash(val content: String) :
    Multihash(Multihash.Type.id, byteArrayOf()) {
    override fun toString() = content
}
