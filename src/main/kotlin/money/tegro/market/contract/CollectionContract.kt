package money.tegro.market.contract

import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class CollectionContract(
    val nextItemIndex: ULong,
    val content: Cell,
    val owner: MsgAddress,

    val workchain_id: Int = 0,
    val itemCode: Cell = ItemContract.NFT_ITEM_CODE,
    val code: Cell = NFT_COLLECTION_CODE,
) {
    fun address(stateInit: StateInit = stateInit()) =
        AddrStd(workchain_id, CellBuilder.createCell { storeTlb(StateInit, stateInit) }.hash())

    fun stateInit(data: Cell = createData()) = StateInit(code, createData())

    fun createData(royaltyData: Cell = RoyaltyContract(0, 0, AddrNone).createData()) = CellBuilder.createCell {
        storeTlb(MsgAddress, owner)
        storeUInt64(nextItemIndex)
        storeRef(content)
        storeRef(itemCode)
        storeRef(royaltyData)
    }


    companion object : KLogging() {
        val NFT_COLLECTION_CODE =
            BagOfCells(
                hex(
                    "B5EE9C724102140100021F000114FF00F4A413F4BCF2C80B0102016202030202CD04050201200E0F04E7D10638048ADF000E8698180B8D848ADF07D201800E98FE99FF6A2687D20699FEA6A6A184108349E9CA829405D47141BAF8280E8410854658056B84008646582A802E78B127D010A65B509E58FE59F80E78B64C0207D80701B28B9E382F970C892E000F18112E001718112E001F181181981E0024060708090201200A0B00603502D33F5313BBF2E1925313BA01FA00D43028103459F0068E1201A44343C85005CF1613CB3FCCCCCCC9ED54925F05E200A6357003D4308E378040F4966FA5208E2906A4208100FABE93F2C18FDE81019321A05325BBF2F402FA00D43022544B30F00623BA9302A402DE04926C21E2B3E6303250444313C85005CF1613CB3FCCCCCCC9ED54002C323401FA40304144C85005CF1613CB3FCCCCCCC9ED54003C8E15D4D43010344130C85005CF1613CB3FCCCCCCC9ED54E05F04840FF2F00201200C0D003D45AF0047021F005778018C8CB0558CF165004FA0213CB6B12CCCCC971FB008002D007232CFFE0A33C5B25C083232C044FD003D0032C03260001B3E401D3232C084B281F2FFF2742002012010110025BC82DF6A2687D20699FEA6A6A182DE86A182C40043B8B5D31ED44D0FA40D33FD4D4D43010245F04D0D431D430D071C8CB0701CF16CCC980201201213002FB5DAFDA89A1F481A67FA9A9A860D883A1A61FA61FF480610002DB4F47DA89A1F481A67FA9A9A86028BE09E008E003E00B01A500C6E"
                )
            ).roots.first()

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
                        nextItemIndex = it.popTinyInt().toULong(), // TODO
                        content = it.popCell(),
                        owner = it.popSlice().loadTlb(MsgAddress),
                    )
                }

        @JvmStatic
        suspend fun itemAddressOf(
            collection: AddrStd,
            index: ULong,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): MsgAddress =
            liteClient.runSmcMethod(
                LiteServerAccountId(collection),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_nft_address_by_index",
                VmStackValue(BigInt(index.toString())) // TODO
            )
                .toMutableVmStack()
                .popSlice()
                .loadTlb(MsgAddress)

        @JvmStatic
        suspend fun itemContent(
            collection: AddrStd,
            index: ULong,
            individualContent: Cell,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): Cell =
            liteClient.runSmcMethod(
                LiteServerAccountId(collection),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_nft_content",
                VmStackValue(BigInt(index.toString())), // TODO
                VmStackValue(individualContent)
            ).toMutableVmStack().popCell()
    }
}
