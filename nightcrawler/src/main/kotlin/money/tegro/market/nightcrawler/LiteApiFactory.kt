package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import money.tegro.market.blockchain.client.ResilientLiteClient
import org.ton.crypto.base64

@Factory
class LiteApiFactory {
    @Prototype
    fun liteApi() = ResilientLiteClient(908566172, 51565, base64("TDg+ILLlRugRB4Kpg3wXjPcoc+d+Eeb7kuVe16CS9z8="))
}
