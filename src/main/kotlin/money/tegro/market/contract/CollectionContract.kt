package money.tegro.market.contract

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import net.logstash.logback.marker.Markers.append
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class CollectionContract(
    val nextItemIndex: Long,
    val content: Cell,
    val owner: MsgAddress,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteApi: LiteApi): CollectionContract {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block {}", kv("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_collection_data")
                .let {
                    logger.trace(append("result", it), "smc method complete {}", kv("exitCode", it.exitCode))
                    require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                    CollectionContract(
                        (it[0] as VmStackValue.TinyInt).value,
                        (it[1] as VmStackValue.Cell).cell,
                        (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress)
                    )
                }
        }

        @JvmStatic
        suspend fun itemAddressOf(collection: AddrStd, index: Long, liteApi: LiteApi): MsgAddress {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block {}", kv("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(
                0b100,
                referenceBlock,
                LiteServerAccountId(collection),
                "get_nft_address_by_index",
                VmStackValue(index)
            )
                .let {
                    logger.trace(append("result", it), "smc method complete {}", kv("exitCode", it.exitCode))
                    require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                    (it.first() as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress)
                }
        }

        @JvmStatic
        suspend fun itemContent(collection: AddrStd, index: Long, individualContent: Cell, liteApi: LiteApi): Cell {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block {}", kv("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(
                0b100,
                referenceBlock,
                LiteServerAccountId(collection),
                "get_nft_content",
                VmStackValue(index),
                VmStackValue(individualContent)
            ).let {
                logger.trace(append("result", it), "smc method complete {}", kv("exitCode", it.exitCode))
                require(it.exitCode == 0) { "failed to run method, exit code is ${it.exitCode}" }

                (it.first() as VmStackValue.Cell).cell
            }
        }
    }
}
