package money.tegro.market.factory

import kotlinx.coroutines.runBlocking
import money.tegro.market.config.LiteApiConfig
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

@Configuration
class LiteClientFactory(private val config: LiteApiConfig) {
    @Bean
    fun liteClient() = runBlocking {
        logger.debug(
            "attempting to connect to {} {} ({})",
            kv("ipv4", config.ipv4),
            kv("port", config.port),
            kv("key", config.key)
        )
        val lc = LiteClient {
            ipv4 = config.ipv4
            port = config.port
            publicKey = base64(config.key)
        }
        lc.start()
        logger.info(
            "lite client {} connected {}",
            kv("serverVersion", lc.serverVersion),
            kv("serverTime", lc.serverTime),
        )
        lc
    }

    companion object : KLogging()
}
