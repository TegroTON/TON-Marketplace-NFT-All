package money.tegro.market.nft

import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.MsgAddressInt
import org.ton.block.VmStackValue
import org.ton.block.tlb.tlbCodec
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTItem(
    val address: MsgAddressInt.AddrStd,
    val initialized: Boolean,
    val index: Long,
    val collection: MsgAddressInt.AddrStd?,
    val owner: MsgAddressInt.AddrStd,
    val content: Cell
) {
    companion object : KoinComponent, KLogging() {
        @JvmStatic
        suspend fun fetch(
            address: MsgAddressInt.AddrStd
        ): NFTItem {
            val liteClient: LiteApi by inject()

            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `get_nft_data` on ${address.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                LiteServerAccountId(address),
                "get_nft_data"
            )

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }
            return NFTItem(
                address,
                (result[0] as VmStackValue.TinyInt).value == -1L,
                (result[1] as VmStackValue.TinyInt).value,
                if (result[2] is VmStackValue.Slice)
                    (result[2] as VmStackValue.Slice).toCellSlice()
                        .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressInt.AddrStd
                else null,
                (result[3] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressInt.AddrStd,
                (result[4] as VmStackValue.Cell).cell
            )
        }
    }
}
