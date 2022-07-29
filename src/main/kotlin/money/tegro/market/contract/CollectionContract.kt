package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb

data class CollectionContract(
    val nextItemIndex: Long,
    val content: Cell,
    val owner: MsgAddress,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteClient: LiteClient): CollectionContract =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_collection_data")
                .let {
                    val exitCode = it.first
                    val stack = it.second?.toMutableVmStack()
                    logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                    if (exitCode != 0)
                        throw ContractException("failed to run method, exit code is ${exitCode}")

                    if (stack == null)
                        throw ContractException("failed to run method, empty response")

                    CollectionContract(
                        owner = stack.popSlice().loadTlb(MsgAddress),
                        content = stack.popCell(),
                        nextItemIndex = stack.popTinyInt(),
                    )
                }

        @JvmStatic
        suspend fun itemAddressOf(collection: AddrStd, index: Long, liteClient: LiteClient): MsgAddress =
            liteClient.runSmcMethod(LiteServerAccountId(collection), "get_nft_address_by_index", VmStackValue(index))
                .let {
                    val exitCode = it.first
                    val stack = it.second?.toMutableVmStack()
                    logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                    if (exitCode != 0)
                        throw ContractException("failed to run method, exit code is ${exitCode}")

                    if (stack == null)
                        throw ContractException("failed to run method, empty response")

                    stack.popSlice().loadTlb(MsgAddress)
                }

        @JvmStatic
        suspend fun itemContent(
            collection: AddrStd,
            index: Long,
            individualContent: Cell,
            liteClient: LiteClient
        ): Cell =
            liteClient.runSmcMethod(
                LiteServerAccountId(collection),
                "get_nft_content",
                VmStackValue(index),
                VmStackValue(individualContent)
            ).let {
                val exitCode = it.first
                val stack = it.second?.toMutableVmStack()
                logger.trace(append("result", stack), "smc method complete {}", kv("exitCode", exitCode))
                if (exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${exitCode}")

                if (stack == null)
                    throw ContractException("failed to run method, empty response")

                stack.popCell()
            }
    }
}
