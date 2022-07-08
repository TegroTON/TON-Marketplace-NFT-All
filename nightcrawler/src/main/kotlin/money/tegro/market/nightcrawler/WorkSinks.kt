package money.tegro.market.nightcrawler

import org.ton.block.AddrStd
import reactor.core.publisher.Sinks

object WorkSinks {
    val accounts: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val collections: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val items: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val royalties: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
    val sales: Sinks.Many<AddrStd> = Sinks.many().unicast().onBackpressureBuffer()
}
