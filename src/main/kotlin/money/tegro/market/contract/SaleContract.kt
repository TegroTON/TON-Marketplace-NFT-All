package money.tegro.market.contract

import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class SaleContract(
    val marketplace: MsgAddress,
    val item: MsgAddress,
    val owner: MsgAddress,
    val fullPrice: BigInt,
    val marketplaceFee: BigInt,
    val royaltyDestination: MsgAddress,
    val royalty: BigInt,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): SaleContract =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_sale_data"
            ).toMutableVmStack().let {
                SaleContract(
                    marketplace = it.popSlice().loadTlb(MsgAddress),
                    item = it.popSlice().loadTlb(MsgAddress),
                    owner = it.popSlice().loadTlb(MsgAddress),
                    fullPrice = it.popNumber().toBigInt(),
                    marketplaceFee = it.popNumber().toBigInt(),
                    royaltyDestination = it.popSlice().loadTlb(MsgAddress),
                    royalty = it.popNumber().toBigInt(),
                )
            }
    }
}
