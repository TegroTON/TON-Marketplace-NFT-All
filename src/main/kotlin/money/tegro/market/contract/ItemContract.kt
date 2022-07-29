package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class ItemContract(
    val initialized: Boolean,
    val index: Long,
    val collection: MsgAddress,
    val owner: MsgAddress,
    val individualContent: Cell
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteClient: LiteClient): ItemContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_nft_data").let {
                val exitCode = it.first
                val stack = it.second?.toMutableVmStack()
                logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                if (exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${exitCode}")

                if (stack == null)
                    throw ContractException("failed to run method, empty response")

                ItemContract(
                    initialized = stack.popTinyInt() == -1L,
                    index = stack.popTinyInt(),
                    collection = stack.popSlice().loadTlb(MsgAddress),
                    owner = stack.popSlice().loadTlb(MsgAddress),
                    individualContent = stack.popCell(),
                )
            }
    }
}
