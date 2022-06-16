package money.tegro.market.nightcrawler.processor

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.lite.api.LiteApi

@Configuration
class ProcessorConfiguration(
    private val liteApi: LiteApi,
) {
    @Bean
    fun collectionInfoUpdateProcessor() = CollectionInfoUpdateProcessor(liteApi)

    @Bean
    fun collectionRoyaltyUpdateProcessor() = CollectionRoyaltyUpdateProcessor(liteApi)

    @Bean
    fun collectionMetadataUpdateProcessor() = CollectionMetadataUpdateProcessor()
}
