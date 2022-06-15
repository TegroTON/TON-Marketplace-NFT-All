package money.tegro.market.nft

import mu.KLogging
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

interface NFTItem {
    val address: MsgAddressIntStd
    val index: Long
    val owner: MsgAddressIntStd
    val individualContent: Cell

    suspend fun content(liteApi: LiteApi): Cell

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteApi: LiteApi,
        ): NFTItem? {
            val referenceBlock = liteApi.getMasterchainInfo().last

            logger.debug("running method `get_nft_data` on ${address.toString(userFriendly = true)}")
            val result = liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_nft_data")

            logger.debug("response: $result")
            if (result.exitCode != 0) {
                logger.warn("Method exit code was ${result.exitCode}. NFT is most likely not initialized")
                return null
            }

            if ((result[0] as VmStackValue.TinyInt).value == -1L) {
                val index = (result[1] as VmStackValue.TinyInt).value
                val collection = (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as? MsgAddressIntStd
                val owner = (result[3] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as MsgAddressIntStd
                val content = (result[4] as VmStackValue.Cell).cell

                return if (collection == null) {
                    NFTDeployedStandaloneItem(address, index, owner, content)
                } else {
                    NFTDeployedCollectionItem(address, index, collection, owner, content)
                }
            } else {
                return null
            }
        }
    }
}

interface NFTDeployedItem : NFTItem

data class NFTDeployedStandaloneItem(
    override val address: MsgAddressIntStd,
    override val index: Long,
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
) : NFTDeployedItem {
    override suspend fun content(liteApi: LiteApi) = individualContent
}

data class NFTDeployedCollectionItem(
    override val address: MsgAddressIntStd,
    override val index: Long,
    val collection: MsgAddressIntStd,
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
) : NFTDeployedItem {
    override suspend fun content(liteApi: LiteApi): Cell {
        val referenceBlock = liteApi.getMasterchainInfo().last

        logger.debug("running method `get_nft_content` on ${collection.toString(userFriendly = true)}")
        val result = liteApi.runSmcMethod(
            0b100,
            referenceBlock,
            LiteServerAccountId(collection),
            "get_nft_content",
            VmStackValue.TinyInt(index),
            VmStackValue.Cell(individualContent)
        )

        logger.debug("response: $result")
        require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

        return (result.first() as VmStackValue.Cell).cell
    }

    companion object : KLogging()
}

interface NFTStubItem : NFTItem {
    companion object {
        val NFT_ITEM_CODE = BagOfCells.of(
            hex(
                "B5EE9C724102140100021F000114FF00F4A413F4BCF2C80B0102016202030202CD04050201200E0F04E7D10638048ADF000E8698180B8D848ADF07D201800E98FE99FF6A2687D20699FEA6A6A184108349E9CA829405D47141BAF8280E8410854658056B84008646582A802E78B127D010A65B509E58FE59F80E78B64C0207D80701B28B9E382F970C892E000F18112E001718112E001F181181981E0024060708090201200A0B00603502D33F5313BBF2E1925313BA01FA00D43028103459F0068E1201A44343C85005CF1613CB3FCCCCCCC9ED54925F05E200A6357003D4308E378040F4966FA5208E2906A4208100FABE93F2C18FDE81019321A05325BBF2F402FA00D43022544B30F00623BA9302A402DE04926C21E2B3E6303250444313C85005CF1613CB3FCCCCCCC9ED54002C323401FA40304144C85005CF1613CB3FCCCCCCC9ED54003C8E15D4D43010344130C85005CF1613CB3FCCCCCCC9ED54E05F04840FF2F00201200C0D003D45AF0047021F005778018C8CB0558CF165004FA0213CB6B12CCCCC971FB008002D007232CFFE0A33C5B25C083232C044FD003D0032C03260001B3E401D3232C084B281F2FFF2742002012010110025BC82DF6A2687D20699FEA6A6A182DE86A182C40043B8B5D31ED44D0FA40D33FD4D4D43010245F04D0D431D430D071C8CB0701CF16CCC980201201213002FB5DAFDA89A1F481A67FA9A9A860D883A1A61FA61FF480610002DB4F47DA89A1F481A67FA9A9A86028BE09E008E003E00B01A500C6E"
            )
        ).roots.first()
    }
}

data class NFTStubStandaloneItem(
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
    val code: Cell = NFTStubItem.NFT_ITEM_CODE,
    val workchainId: Int = owner.workchainId,
    override val index: Long = 0L,
) : NFTStubItem {
    private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }
    private val stateInitCodec by lazy { StateInit.tlbCodec() }

    override val address: MsgAddressIntStd
        get() = MsgAddressIntStd(workchainId, CellBuilder.createCell { storeTlb(stateInitCodec, stateInit()) }.hash())

    override suspend fun content(liteApi: LiteApi) = individualContent

    fun stateInit() = StateInit(createCode(), createData())

    fun createCode() = code

    fun createData(): Cell = CellBuilder.createCell {
        storeUInt(index, 64)
        storeTlb(msgAddressCodec, MsgAddressExtNone)
        storeTlb(msgAddressCodec, owner)
        storeRef(individualContent)
    }
}
