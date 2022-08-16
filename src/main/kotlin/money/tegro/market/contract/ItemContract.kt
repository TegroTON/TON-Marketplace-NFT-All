package money.tegro.market.contract

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import money.tegro.market.serializer.CellSerializer
import money.tegro.market.serializer.MsgAddressSerializer
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class ItemContract(
    val initialized: Boolean,
    val index: Long,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val collection: MsgAddress,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val owner: MsgAddress,
    @JsonSerialize(using = CellSerializer::class)
    val individualContent: Cell
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): ItemContract =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_nft_data"
            ).toMutableVmStack().let {
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
