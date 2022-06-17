package money.tegro.market.nightcrawler.processor

import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.ton.LiteApiFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProcessorConfiguration(
    private val liteApiFactory: LiteApiFactory,

    private val collectionInfoRepository: CollectionInfoRepository,

    private val itemInfoRepository: ItemInfoRepository,
) {
    @Bean
    fun collectionInfoUpdateProcessor() = CollectionInfoUpdateProcessor(liteApiFactory)

    @Bean
    fun collectionRoyaltyUpdateProcessor() = CollectionRoyaltyUpdateProcessor(liteApiFactory)

    @Bean
    fun collectionMetadataUpdateProcessor() = CollectionMetadataUpdateProcessor()

    @Bean
    fun primeCollectionInfoProcessor() = PrimeCollectionInfoProcessor(collectionInfoRepository)

    @Bean
    fun collectionMissingItemsProcessor() =
        CollectionMissingItemsProcessor(liteApiFactory, itemInfoRepository)

    @Bean
    fun itemInfoUpdateProcessor() = ItemInfoUpdateProcessor(liteApiFactory, collectionInfoRepository)

    @Bean
    fun itemRoyaltyUpdateProcessor() = ItemRoyaltyUpdateProcessor(liteApiFactory)

    @Bean
    fun itemMetadataUpdateProcessor() = ItemMetadataUpdateProcessor(liteApiFactory)

    @Bean
    fun itemSaleUpdateProcessor() = ItemSaleUpdateProcessor(liteApiFactory)

    @Bean
    fun primeItemInfoProcessor() = PrimeItemInfoProcessor(itemInfoRepository)
}
