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

interface NFTSale {
    val address: MsgAddressIntStd
    val marketplace: MsgAddressIntStd
    val item: MsgAddressIntStd
    val owner: MsgAddressIntStd
    val price: Long
    val marketplaceFee: Long
    val royaltyDestination: MsgAddressIntStd?
    val royalty: Long?

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteApi: LiteApi,
        ): NFTSale? {
            val referenceBlock = liteApi.getMasterchainInfo().last

            logger.debug("running method `get_sale_data` on ${address.toString(userFriendly = true)}")
            val result = liteApi.runSmcMethod(
                0b100, referenceBlock,
                LiteServerAccountId(address),
                "get_sale_data"
            )

            logger.debug("response: $result")
            if (result.exitCode != 0) {
                logger.debug { "contract doesn't have such method/method execution failed, assume not an NFTSale contract" }
                return null
            }

            return try {
                NFTDeployedSale(
                    address,
                    (result[0] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[1] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[3] as VmStackValue.TinyInt).value,
                    (result[4] as VmStackValue.TinyInt).value,
                    (result[5] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as? MsgAddressIntStd,
                    (result[6] as VmStackValue.TinyInt).value,
                )
            } catch (e: Exception) {
                logger.warn { "couldn't parse response: $e" }
                null
            }
        }
    }
}

data class NFTDeployedSale(
    override val address: MsgAddressIntStd,
    override val marketplace: MsgAddressIntStd,
    override val item: MsgAddressIntStd,
    override val owner: MsgAddressIntStd,
    override val price: Long,
    override val marketplaceFee: Long,
    override val royaltyDestination: MsgAddressIntStd?,
    override val royalty: Long?,
) : NFTSale


// Custom NFT sale contract on steroids
// Implements basic nft sale interface as well
data class NFTStubSidorovich(
    override val marketplace: MsgAddressIntStd,
    override val item: MsgAddressIntStd,
    override val owner: MsgAddressIntStd,
    override val price: Long,
    override val marketplaceFee: Long,
    override val royaltyDestination: MsgAddressIntStd?,
    override val royalty: Long?,

    val code: Cell = NFT_SALE_CODE,
    val workchainId: Int = owner.workchainId,
) : NFTSale {
    private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }
    private val coinsCodec by lazy { Coins.tlbCodec() }
    private val stateInitCodec by lazy { StateInit.tlbCodec() }

    override val address: MsgAddressIntStd
        get() = MsgAddressIntStd(workchainId, CellBuilder.createCell { storeTlb(stateInitCodec, stateInit()) }.hash())

    fun stateInit() = StateInit(createCode(), createData())

    fun createCode() = code

    fun createData(): Cell = CellBuilder.createCell {
        storeTlb(msgAddressCodec, marketplace)
        storeTlb(msgAddressCodec, item)
        storeTlb(msgAddressCodec, owner)
        storeTlb(coinsCodec, Coins.ofNano(price))
        storeRef {
            storeTlb(coinsCodec, Coins.ofNano(marketplaceFee))
            storeTlb(msgAddressCodec, royaltyDestination ?: MsgAddressExtNone)
            storeTlb(coinsCodec, Coins.ofNano(royalty ?: 0))
        }
        storeUInt(0, 1) // Custom contract uses this bit to check if it was initialized
    }

    companion object {
        val NFT_SALE_CODE = BagOfCells.of(
            hex(
                "B5EE9C7241020A010001A5000114FF00F4A413F4BCF2C80B0102012002030201480405005CF230ED44D0FA40FA40FA40FA00D4D30030C000F2E045F80071C85006CF165004CF1658CF1601FA02CCCB00C9ED540202CD06070035A03859DA89A1F481F481F481F401A9A6006061A1F401F481F4006101B5D00E8698180B8D8492F82707D201876A2687D207D207D207D006A6980186000F970229363804C9B081B2299823878027003698FE99F9810E000C92F857010E0014D188823881B22A009780270191B1B826001F1812F834207F97840801F5D41081DCD650029285029185F7970E101E87D007D207D0018384008646582A804E78B28B9D090D0A85AD08A500AFD010AE5B564B8FD80384008646582AC678B2803FD010B65B564B8FD80384008646582A802E78B00FD0109E5B564B8FD80381041082FE61E8A10C00C646582A802E78B117D010A65B509E58F8A409008E82103B9ACA0015BEF2E1C95312C70559C705B1F2E1CA702082105FCC3D14218010C8CB055006CF1622FA0215CB6A14CB1F14CB3F21CF1601CF16CA0021FA02CA00C98100A0FB00002ACB3F22CF1658CF16CA0021FA02CA00C98100A0FB0072310FAD"
            )
        ).roots.first()
    }
}




