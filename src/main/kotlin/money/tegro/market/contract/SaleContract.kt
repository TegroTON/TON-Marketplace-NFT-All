package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackNumber
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
        suspend fun of(address: AddrStd, liteClient: LiteClient): SaleContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_sale_data").let {
                val exitCode = it.first
                val stack = it.second?.toMutableVmStack()
                logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                if (exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${exitCode}")

                if (stack == null)
                    throw ContractException("failed to run method, empty response")

                SaleContract(
                    royalty = (stack.pop() as VmStackNumber).toBigInt(),
                    royaltyDestination = stack.popSlice().loadTlb(MsgAddress),
                    marketplaceFee = (stack.pop() as VmStackNumber).toBigInt(),
                    fullPrice = (stack.pop() as VmStackNumber).toBigInt(),
                    owner = stack.popSlice().loadTlb(MsgAddress),
                    item = stack.popSlice().loadTlb(MsgAddress),
                    marketplace = stack.popSlice().loadTlb(MsgAddress),
                )
            }
    }
}
