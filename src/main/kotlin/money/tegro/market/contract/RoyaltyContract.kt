package money.tegro.market.contract

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import money.tegro.market.serializer.MsgAddressSerializer
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class RoyaltyContract(
    val numerator: Int,
    val denominator: Int,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val destination: MsgAddress,
) {
    fun value() = numerator.toFloat() / denominator

    companion object : KLogging() {
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
                    numerator = it.popTinyInt().toInt(),
                    denominator = it.popTinyInt().toInt(),
                    destination = it.popSlice().loadTlb(MsgAddress),
                )
            }
    }
}
