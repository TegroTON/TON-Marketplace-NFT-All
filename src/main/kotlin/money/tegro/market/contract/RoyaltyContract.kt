package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class RoyaltyContract(
    val numerator: Int,
    val denominator: Int,
    val destination: MsgAddress,
) {
    fun value() = numerator.toFloat() / denominator

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteApi: LiteApi): RoyaltyContract {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block {}", kv("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "royalty_params").let {
                logger.trace(append("result", it), "smc method complete {}", kv("exitCode", it.exitCode))
                if (it.exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${it.exitCode}")

                RoyaltyContract(
                    (it[0] as VmStackValue.TinyInt).value.toInt(),
                    (it[1] as VmStackValue.TinyInt).value.toInt(),
                    (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress)
                )
            }
        }
    }
}
