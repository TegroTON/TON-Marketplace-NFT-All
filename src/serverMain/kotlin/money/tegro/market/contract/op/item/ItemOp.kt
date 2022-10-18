package money.tegro.market.contract.op.item

import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

sealed interface ItemOp {
    companion object : TlbCodec<ItemOp> by ItemOpCombinator
}

private object ItemOpCombinator : TlbCombinator<ItemOp>() {
    override val constructors: List<TlbConstructor<out ItemOp>> =
        listOf(TransferOp.tlbCodec())

    override fun getConstructor(value: ItemOp): TlbConstructor<out ItemOp> = when (value) {
        is TransferOp -> TransferOp.tlbCodec()
    }
}
