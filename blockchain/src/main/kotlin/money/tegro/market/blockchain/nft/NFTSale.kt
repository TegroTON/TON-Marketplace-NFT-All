package money.tegro.market.blockchain.nft

import money.tegro.market.blockchain.referenceBlock
import mu.KLogging
import mu.withLoggingContext
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

interface NFTSale : Addressable {
    val marketplace: MsgAddress
    val item: MsgAddress
    val owner: MsgAddress
    val fullPrice: Long
    val marketplaceFee: Long
    val royaltyDestination: MsgAddress
    val royalty: Long

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock(),
        ): NFTSale =
            withLoggingContext(
                "address" to address.toString(userFriendly = true, bounceable = true)
            ) {
                liteApi.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "get_sale_data").let {
                    if (it.exitCode != 0) {
                        logger.warn { "failed to run method" }
                        throw NFTException("failed to run method, exit code is ${it.exitCode}")
                    }

                    NFTSaleImpl(
                        address,
                        (it[0] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                        (it[1] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                        (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                        (it[3] as VmStackValue.TinyInt).value,
                        (it[4] as VmStackValue.TinyInt).value,
                        (it[5] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                        (it[6] as VmStackValue.TinyInt).value,
                    )
                }
            }
    }
}

private data class NFTSaleImpl(
    override val address: MsgAddress,
    override val marketplace: MsgAddress,
    override val item: MsgAddress,
    override val owner: MsgAddress,
    override val fullPrice: Long,
    override val marketplaceFee: Long,
    override val royaltyDestination: MsgAddress,
    override val royalty: Long
) : NFTSale
