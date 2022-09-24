package money.tegro.market.configuration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.ton.adnl.client.engine.cio.CIOAdnlClientEngine
import org.ton.lite.client.LiteClient
import org.ton.logger.Logger

@Configuration
class LiteClientConfiguration(
    @Value("\${lite-client.config:classpath:config-sandbox.json}")
    private val jsonConfig: Resource
) {
    @OptIn(ExperimentalSerializationApi::class)
    @Bean
    fun liteClient() = LiteClient(
        CIOAdnlClientEngine.create(),
        Json {
            ignoreUnknownKeys = true
        }
            .decodeFromStream(jsonConfig.inputStream),
        TonLogger()
    )

    companion object : KLogging()
}

private class TonLogger(override var level: Logger.Level = Logger.Level.DEBUG) : Logger {
    override fun log(level: Logger.Level, message: () -> String) {
        when (level) {
            Logger.Level.DEBUG -> {
                logger.debug(message)
            }

            Logger.Level.FATAL -> {
                logger.error(message)
            }

            Logger.Level.INFO -> {
                logger.info(message)
            }

            Logger.Level.WARN -> {
                logger.warn(message)
            }
        }
    }

    companion object : KLogging()
}
