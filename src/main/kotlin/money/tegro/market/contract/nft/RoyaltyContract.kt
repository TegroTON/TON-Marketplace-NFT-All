package money.tegro.market.contract.nft

import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class RoyaltyContract(
    val numerator: Int,
    val denominator: Int,
    val destination: MsgAddress,
) {
    fun value() = numerator.toDouble() / denominator

    companion object {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): RoyaltyContract =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                referenceBlock ?: liteClient.getLastBlockId(),
                "royalty_params"
            ).toMutableVmStack().let {
                RoyaltyContract(
                    numerator = it.popNumber().toInt(),
                    denominator = it.popNumber().toInt(),
                    destination = it.popSlice().loadTlb(MsgAddress),
                )
            }
    }
}
