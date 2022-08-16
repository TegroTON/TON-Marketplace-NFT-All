package money.tegro.market.contract

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import money.tegro.market.serializer.CellSerializer
import money.tegro.market.serializer.MsgAddressSerializer
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class CollectionContract(
    val nextItemIndex: Long,
    @JsonSerialize(using = CellSerializer::class)
    val content: Cell,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val owner: MsgAddress,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): CollectionContract =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_collection_data"
            )
                .toMutableVmStack()
                .let {
                    CollectionContract(
                        nextItemIndex = it.popTinyInt(),
                        content = it.popCell(),
                        owner = it.popSlice().loadTlb(MsgAddress),
                    )
                }

        @JvmStatic
        suspend fun itemAddressOf(
            collection: AddrStd,
            index: Long,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): MsgAddress =
            liteClient.runSmcMethod(
                LiteServerAccountId(collection),
                referenceBlock ?: liteClient.getLastBlockId(), "get_nft_address_by_index", VmStackValue(index)
            )
                .toMutableVmStack()
                .popSlice()
                .loadTlb(MsgAddress)

        @JvmStatic
        suspend fun itemContent(
            collection: AddrStd,
            index: Long,
            individualContent: Cell,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): Cell =
            liteClient.runSmcMethod(
                LiteServerAccountId(collection),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_nft_content",
                VmStackValue(index),
                VmStackValue(individualContent)
            ).toMutableVmStack().popCell()
    }
}
