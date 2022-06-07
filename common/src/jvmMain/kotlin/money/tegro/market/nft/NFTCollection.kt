package money.tegro.market.nft

import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressIntStd
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTCollection(
    override val address: MsgAddressIntStd,
    val nextItemIndex: Long,
    val content: Cell,
    val owner: MsgAddressIntStd
) : NFT {
    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteClient: LiteApi,
            referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
        ): NFTCollection {
            logger.debug("running method `get_collection_data` on ${address.toString(userFriendly = true)}")
            val result =
                liteClient.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_collection_data")

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return NFTCollection(
                address,
                (result[0] as VmStackValue.TinyInt).value,
                (result[1] as VmStackValue.Cell).cell,
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as MsgAddressIntStd
            )
        }
    }
}
