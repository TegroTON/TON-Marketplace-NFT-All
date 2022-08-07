package money.tegro.market.contract

import mu.KLogging
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
    fun value() = numerator.toFloat() / denominator

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteClient: LiteClient): RoyaltyContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "royalty_params").toMutableVmStack().let {
                RoyaltyContract(
                    numerator = it.popTinyInt().toInt(),
                    denominator = it.popTinyInt().toInt(),
                    destination = it.popSlice().loadTlb(MsgAddress),
                )
            }
    }
}
