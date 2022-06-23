package money.tegro.market.core.bean

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.core.configuration.LiteApiConfiguration
import org.ton.crypto.base64

@Factory
class LiteApiBean(private var configuration: LiteApiConfiguration) {
    @Prototype
    fun liteApi() = ResilientLiteClient(configuration.ipv4, configuration.port, base64(configuration.key))
}
