package money.tegro.market.nightcrawler

import org.ton.block.AddrStd
import reactor.core.publisher.Sinks

object WorkSinks {
    val accounts: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val collections: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val items: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val royalties: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val sales: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()

    fun emitNextAccount(t: AddrStd) = guaranteedEmitNext(accounts, t)
    fun emitNextCollection(t: AddrStd) = guaranteedEmitNext(collections, t)
    fun emitNextItem(t: AddrStd) = guaranteedEmitNext(items, t)
    fun emitNextRoyalty(t: AddrStd) = guaranteedEmitNext(royalties, t)
    fun emitNextSale(t: AddrStd) = guaranteedEmitNext(sales, t)

    fun <T> guaranteedEmitNext(sink: Sinks.Many<T>, value: T) {
        sink.emitNext(value) { _, emitResult ->
            emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED)
        }
    }
}
