package money.tegro.market.contract.op.collection

import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

sealed interface CollectionOp {
    companion object : TlbCodec<CollectionOp> by CollectionOpCombinator
}

private object CollectionOpCombinator : TlbCombinator<CollectionOp>() {
    override val constructors: List<TlbConstructor<out CollectionOp>> =
        listOf(DeployItemOp.tlbCodec(), ChangeOwnerOp.tlbCodec())

    override fun getConstructor(value: CollectionOp): TlbConstructor<out CollectionOp> = when (value) {
        is DeployItemOp -> DeployItemOp.tlbCodec()
        is ChangeOwnerOp -> ChangeOwnerOp.tlbCodec()
    }
}
