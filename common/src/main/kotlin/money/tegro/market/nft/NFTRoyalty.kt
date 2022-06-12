package money.tegro.market.nft

import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.MsgAddressInt
import org.ton.block.MsgAddressIntStd
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTRoyalty(
    val address: MsgAddressIntStd,
    val numerator: Int? = null,
    val denominator: Int? = null,
    val destination: MsgAddressIntStd? = null,
) {
    fun value() = numerator?.let { n -> denominator?.let { d -> n.toFloat() / d } }

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteClient: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last }
        ): NFTRoyalty {
            logger.debug("running method `royalty_params` on ${address.toString(userFriendly = true)}")
            val result =
                liteClient.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "royalty_params")

            logger.debug("response: $result")
            if (result.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection/Item doesn't implement NFTRoyalty extension - its ok
                logger.debug("contract doesn't implement the NFTRoyalty extension")
                return NFTRoyalty(address)
            }

            if (result.exitCode != 0) {
                logger.warn { "Failed to run the method, exit code is ${result.exitCode}. Contract might be uninitialized" }
                return NFTRoyalty(address)
            }

            return NFTRoyalty(
                address,
                (result[0] as VmStackValue.TinyInt).value.toInt(),
                (result[1] as VmStackValue.TinyInt).value.toInt(),
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressIntStd
            )
        }
    }
}
