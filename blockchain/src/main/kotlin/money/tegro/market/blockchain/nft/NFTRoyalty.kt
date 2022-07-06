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

data class NFTRoyalty(
    override val address: MsgAddress,
    val numerator: Int,
    val denominator: Int,
    val destination: MsgAddress,
) : Addressable {
    fun value() = numerator.toFloat() / denominator

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock(),
        ): NFTRoyalty =
            withLoggingContext(
                "address" to address.toString(userFriendly = true, bounceable = true)
            ) {
                liteApi.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "royalty_params").let {
                    if (it.exitCode != 0) {
                        logger.warn { "failed to run method" }
                        throw NFTException("failed to run method, exit code is ${it.exitCode}")
                    }

                    NFTRoyalty(
                        address,
                        (it[0] as VmStackValue.TinyInt).value.toInt(),
                        (it[1] as VmStackValue.TinyInt).value.toInt(),
                        (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec)
                    )
                }
            }
    }
}
