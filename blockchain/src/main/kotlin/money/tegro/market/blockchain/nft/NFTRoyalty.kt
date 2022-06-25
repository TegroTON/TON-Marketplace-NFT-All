package money.tegro.market.blockchain.nft

import mu.KLogging
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTRoyalty(
    val numerator: Int,
    val denominator: Int,
    val destination: AddrStd,
) {
    fun value() = numerator.toFloat() / denominator

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteApi
        ): NFTRoyalty? {
            val referenceBlock = liteClient.getMasterchainInfo().last

            logger.debug("running method `royalty_params` on ${address.toString(userFriendly = true)}")
            val result =
                liteClient.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "royalty_params")

            logger.debug("response: $result")
            if (result.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection/Item doesn't implement NFTRoyalty extension - its ok
                logger.debug("contract doesn't implement the NFTRoyalty extension")
                return null
            }

            if (result.exitCode != 0) {
                logger.warn { "Failed to run the method, exit code is ${result.exitCode}. Contract might be uninitialized" }
                return null
            }

            return NFTRoyalty(
                (result[0] as VmStackValue.TinyInt).value.toInt(),
                (result[1] as VmStackValue.TinyInt).value.toInt(),
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(MsgAddressInt.tlbCodec()) as AddrStd
            )
        }
    }
}
