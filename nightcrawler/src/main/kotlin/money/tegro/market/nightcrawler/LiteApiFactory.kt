package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import money.tegro.market.blockchain.client.ResilientLiteClient
import org.ton.crypto.base64

@Factory
class LiteApiFactory(private var configuration: LiteApiFactoryConfiguration) {
    @Prototype
    fun liteApi() = ResilientLiteClient(configuration.ipv4, configuration.port, base64(configuration.key))
}
