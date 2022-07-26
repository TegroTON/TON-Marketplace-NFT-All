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

data class SaleContract(
    val marketplace: MsgAddress,
    val item: MsgAddress,
    val owner: MsgAddress,
    val fullPrice: Long,
    val marketplaceFee: Long,
    val royaltyDestination: MsgAddress,
    val royalty: Long,
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun of(address: AddrStd, liteApi: LiteApi): SaleContract {
            val referenceBlock = liteApi.getMasterchainInfo().last
            logger.trace("reference block {}", kv("seqno", referenceBlock.seqno))

            return liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_nft_content").let {
                logger.trace(append("result", it), "smc method complete {}", kv("exitCode", it.exitCode))
                if (it.exitCode != 0)
                    throw ContractException("failed to run method, exit code is ${it.exitCode}")
                
                SaleContract(
                    (it[0] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress),
                    (it[1] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress),
                    (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress),
                    (it[3] as VmStackValue.TinyInt).value,
                    (it[4] as VmStackValue.TinyInt).value,
                    (it[5] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddress),
                    (it[6] as VmStackValue.TinyInt).value,
                )
            }
        }
    }
}
