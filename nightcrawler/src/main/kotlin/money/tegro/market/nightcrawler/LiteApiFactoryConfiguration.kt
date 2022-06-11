package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.ton.LiteApiFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.crypto.base64

@Configuration
class LiteApiFactoryConfiguration {
    @Bean
    fun liteApiFactory(): LiteApiFactory {
        return runBlocking {
            val factory = LiteApiFactory()
            factory.setConnectionParameters(908566172, 51565, base64("TDg+ILLlRugRB4Kpg3wXjPcoc+d+Eeb7kuVe16CS9z8="))
            factory
        }
    }

    @Bean
    fun liteApi() = liteApiFactory().getObject()
}
