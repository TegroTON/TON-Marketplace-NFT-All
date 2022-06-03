package money.tegro.market.nft

import mu.KLogging
import org.ton.block.MsgAddressInt
import org.ton.block.MsgAddressIntStd
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

interface NFT {
    companion object : KLogging() {
        @JvmStatic
        suspend fun getRoyaltyParameters(
            liteClient: LiteApi,
            address: MsgAddressIntStd
        ): Triple<Int, Int, MsgAddressIntStd>? {
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `royalty_params` on ${address.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                LiteServerAccountId(address),
                "royalty_params"
            )

            logger.debug("response: $result")
            if (result.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection/Item doesn't implement NFTRoyalty extension - its ok
                logger.debug("contract doesn't implement the NFTRoyalty extension")
                return null
            }

            require(result.exitCode == 0) {
                "Failed to run the method, exit code is ${result.exitCode}"
            }

            return Triple(
                (result[0] as VmStackValue.TinyInt).value.toInt(),
                (result[1] as VmStackValue.TinyInt).value.toInt(),
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressIntStd
            )
        }
    }
}
