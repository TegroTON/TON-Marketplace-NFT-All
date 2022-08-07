package money.tegro.market.contract

import mu.KLogging
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class ItemContract(
    val initialized: Boolean,
    val index: Long,
    val collection: MsgAddress,
    val owner: MsgAddress,
    val individualContent: Cell
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteClient: LiteClient): ItemContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_nft_data").toMutableVmStack().let {
                ItemContract(
                    initialized = it.popTinyInt() == -1L,
                    index = it.popTinyInt(),
                    collection = it.popSlice().loadTlb(MsgAddress),
                    owner = it.popSlice().loadTlb(MsgAddress),
                    individualContent = it.popCell(),
                )
            }
    }
}
