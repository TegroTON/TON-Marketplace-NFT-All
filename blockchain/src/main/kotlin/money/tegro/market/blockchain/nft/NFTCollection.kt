package money.tegro.market.blockchain.nft

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.StateInit
import org.ton.block.VmStackValue
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

interface NFTCollection {
    val address: AddrStd
    val nextItemIndex: Long
    val content: Cell
    val owner: AddrStd

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        ): NFTDeployedCollection {
            logger.debug("running method `get_collection_data` on ${address.toString(userFriendly = true)}")
            val result =
                liteApi.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "get_collection_data")

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return NFTDeployedCollection(
                address,
                (result[0] as VmStackValue.TinyInt).value,
                (result[1] as VmStackValue.Cell).cell,
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as AddrStd
            )
        }
    }
}

data class NFTDeployedCollection(
    override val address: AddrStd,
    override val nextItemIndex: Long,
    override val content: Cell,
    override val owner: AddrStd
) : NFTCollection {
    suspend fun itemAddresses(liteApi: LiteApi) =
        (0 until nextItemIndex).asFlow()
            .map { itemAddress(it, liteApi) }

    suspend fun items(liteApi: LiteApi) = itemAddresses(liteApi).map {
        NFTItem.of(it, liteApi)
    }

    suspend fun itemAddress(index: Long, liteApi: LiteApi) = itemAddressOf(this.address, index, liteApi)

    suspend fun item(index: Long, liteApi: LiteApi) = NFTItem.of(this.itemAddress(index, liteApi), liteApi)

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        suspend fun itemAddressOf(
            collection: AddrStd,
            index: Long,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        ): AddrStd {
            logger.debug("running method `get_nft_address_by_index` on ${collection.toString(userFriendly = true)} for index $index")
            val result = liteApi.runSmcMethod(
                0b100,
                referenceBlock(),
                LiteServerAccountId(collection),
                "get_nft_address_by_index",
                VmStackValue.TinyInt(index)
            )

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return (result.first() as VmStackValue.Slice).toCellSlice()
                .loadTlb(msgAddressCodec) as AddrStd
        }
    }
}

data class NFTStubCollection(
    override val owner: AddrStd,
    val collectionContent: Cell,
    val commonContent: Cell = Cell.of(),
    val royalty: NFTRoyalty? = null,
    val itemCode: Cell = NFTStubItem.NFT_ITEM_CODE,
    val code: Cell = NFT_COLLECTION_CODE,
    val workchainId: Int = owner.workchain_id,
) : NFTCollection {
    private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }
    private val stateInitCodec by lazy { StateInit.tlbCodec() }
    private var _nextItemIndex = 0L

    override val address: AddrStd
        get() = AddrStd(workchainId, CellBuilder.createCell { storeTlb(stateInitCodec, stateInit()) }.hash())

    override val content: Cell = collectionContent

    override val nextItemIndex: Long
        get() = _nextItemIndex

    fun stateInit() = StateInit(createCode(), createData())

    fun createCode() = code

    fun createData(): Cell = CellBuilder.createCell {
//        ;; default#_ royalty_factor:uint16 royalty_base:uint16 royalty_address:MsgAddress = RoyaltyParams;
//        ;; storage#_ owner_address:MsgAddress next_item_index:uint64
//        ;;           ^[collection_content:^Cell common_content:^Cell]
//        ;;           nft_item_code:^Cell
//        ;;           royalty_params:^RoyaltyParams
//        ;;           = Storage;
        storeTlb(msgAddressCodec, owner)
        storeUInt(nextItemIndex, 64)
        storeRef {
            storeRef(collectionContent)
            storeRef(commonContent)
        }
        storeRef {
            storeRef(itemCode)
        }
        storeRef {
            royalty?.let {
                storeUInt(it.numerator, 16)
                storeUInt(it.denominator, 16)
                storeTlb(msgAddressCodec, it.destination)
            }
        }
    }

    companion object {
        val NFT_COLLECTION_CODE = BagOfCells.of(
            hex(
                "B5EE9C724102140100021F000114FF00F4A413F4BCF2C80B0102016202030202CD04050201200E0F04E7D10638048ADF000E8698180B8D848ADF07D201800E98FE99FF6A2687D20699FEA6A6A184108349E9CA829405D47141BAF8280E8410854658056B84008646582A802E78B127D010A65B509E58FE59F80E78B64C0207D80701B28B9E382F970C892E000F18112E001718112E001F181181981E0024060708090201200A0B00603502D33F5313BBF2E1925313BA01FA00D43028103459F0068E1201A44343C85005CF1613CB3FCCCCCCC9ED54925F05E200A6357003D4308E378040F4966FA5208E2906A4208100FABE93F2C18FDE81019321A05325BBF2F402FA00D43022544B30F00623BA9302A402DE04926C21E2B3E6303250444313C85005CF1613CB3FCCCCCCC9ED54002C323401FA40304144C85005CF1613CB3FCCCCCCC9ED54003C8E15D4D43010344130C85005CF1613CB3FCCCCCCC9ED54E05F04840FF2F00201200C0D003D45AF0047021F005778018C8CB0558CF165004FA0213CB6B12CCCCC971FB008002D007232CFFE0A33C5B25C083232C044FD003D0032C03260001B3E401D3232C084B281F2FFF2742002012010110025BC82DF6A2687D20699FEA6A6A182DE86A182C40043B8B5D31ED44D0FA40D33FD4D4D43010245F04D0D431D430D071C8CB0701CF16CCC980201201213002FB5DAFDA89A1F481A67FA9A9A860D883A1A61FA61FF480610002DB4F47DA89A1F481A67FA9A9A86028BE09E008E003E00B01A500C6E"
            )
        ).roots.first()
    }
}
