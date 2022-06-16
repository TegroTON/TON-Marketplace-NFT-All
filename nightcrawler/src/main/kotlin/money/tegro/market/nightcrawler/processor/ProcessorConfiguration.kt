package money.tegro.market.nightcrawler.processor

import money.tegro.market.db.CollectionInfoRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.lite.api.LiteApi

@Configuration
class ProcessorConfiguration(
    private val liteApi: LiteApi,

    private val collectionInfoRepository: CollectionInfoRepository,
) {
    @Bean
    fun collectionInfoUpdateProcessor() = CollectionInfoUpdateProcessor(liteApi)

    @Bean
    fun collectionRoyaltyUpdateProcessor() = CollectionRoyaltyUpdateProcessor(liteApi)

    @Bean
    fun collectionMetadataUpdateProcessor() = CollectionMetadataUpdateProcessor()

    @Bean
    fun primeCollectionInfoProcessor() = PrimeCollectionInfoProcessor(collectionInfoRepository)
}
