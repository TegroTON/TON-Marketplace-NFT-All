package money.tegro.market.op

import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

sealed interface ItemOp {
    companion object : TlbCodec<ItemOp> by ItemOpCombinator
}

private object ItemOpCombinator : TlbCombinator<ItemOp>() {
    override val constructors: List<TlbConstructor<out ItemOp>> =
        listOf(TransferItemOp.tlbCodec())

    override fun getConstructor(value: ItemOp): TlbConstructor<out ItemOp> = when (value) {
        is TransferItemOp -> TransferItemOp.tlbCodec()
    }
}
