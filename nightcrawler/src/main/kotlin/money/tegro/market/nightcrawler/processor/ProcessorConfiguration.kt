package money.tegro.market.nightcrawler.processor

import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.nightcrawler.CollectionInfoUpdateProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.lite.api.LiteApi

@Configuration
class ProcessorConfiguration(
    private val liteApi: LiteApi,

    private val collectionInfoRepository: CollectionInfoRepository,
) {
    @Bean
    fun collectionInfoUpdateProcessor() = CollectionInfoUpdateProcessor(liteApi, collectionInfoRepository)
}
