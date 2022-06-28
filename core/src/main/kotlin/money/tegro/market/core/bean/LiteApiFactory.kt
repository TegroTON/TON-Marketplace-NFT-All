package money.tegro.market.core.bean

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.core.configuration.LiteApiConfiguration
import org.ton.crypto.base64

@Factory
class LiteApiFactory(private var configuration: LiteApiConfiguration) {
    private val liteApi = runBlocking {
        ResilientLiteClient(configuration.ipv4, configuration.port, base64(configuration.key)).connect()
    }

    @Singleton
    fun liteApi() = liteApi
}
