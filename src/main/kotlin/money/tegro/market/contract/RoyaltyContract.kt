package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
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
            liteClient.runSmcMethod(LiteServerAccountId(address), "royalty_params").let {
                val exitCode = it.first
                val stack = it.second?.toMutableVmStack()
                logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                if (exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${exitCode}")

                if (stack == null)
                    throw ContractException("failed to run method, empty response")

                RoyaltyContract(
                    destination = stack.popSlice().loadTlb(MsgAddress),
                    denominator = stack.popTinyInt().toInt(),
                    numerator = stack.popTinyInt().toInt(),
                )
            }
    }
}
