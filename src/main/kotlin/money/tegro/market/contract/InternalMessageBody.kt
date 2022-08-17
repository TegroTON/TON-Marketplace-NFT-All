package money.tegro.market.contract

import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

sealed interface InternalMessageBody {
    companion object : TlbCodec<InternalMessageBody> by InternalMessageBodyCombinator
}

private object InternalMessageBodyCombinator : TlbCombinator<InternalMessageBody>() {
    override val constructors: List<TlbConstructor<out InternalMessageBody>> =
        listOf(OpTransfer.tlbCodec(), OpDeployNFT.tlbCodec())

    override fun getConstructor(value: InternalMessageBody): TlbConstructor<out InternalMessageBody> = when (value) {
        is OpTransfer -> OpTransfer.tlbCodec()
        is OpDeployNFT -> OpDeployNFT.tlbCodec()
        else -> TODO()
    }
}
