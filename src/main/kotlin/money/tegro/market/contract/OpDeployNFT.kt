package money.tegro.market.contract

import org.ton.block.Coins
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class OpDeployNFT(
    val query_id: ULong,
    val index: ULong,
    val amount: Coins,
    val content: Cell,
) : InternalMessageBody {
    companion object : TlbCodec<OpDeployNFT> by OpDeployNFTConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<OpDeployNFT> = OpDeployNFTConstructor
    }
}

private object OpDeployNFTConstructor : TlbConstructor<OpDeployNFT>(
    schema = "transfer#00000001 index:uint64 amount:Coins content:^Cell = InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: OpDeployNFT) {
        cellBuilder.apply {
            storeUInt64(value.query_id)
            storeUInt64(value.index)
            storeTlb(Coins, value.amount)
            storeRef(value.content)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): OpDeployNFT = cellSlice.run {
        OpDeployNFT(
            query_id = loadUInt64(),
            index = loadUInt64(),
            amount = loadTlb(Coins),
            content = loadRef(),
        )
    }
}
