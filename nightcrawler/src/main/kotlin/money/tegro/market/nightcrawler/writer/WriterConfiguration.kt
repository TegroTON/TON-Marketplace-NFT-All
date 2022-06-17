package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class WriterConfiguration(private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    fun collectionInfoWriter() = CollectionInfoWriter(entityManagerFactory)

    @Bean
    fun collectionInfoAsyncWriter() = CollectionInfoAsyncWriter(collectionInfoWriter())

    @Bean
    fun collectionRoyaltyWriter() = CollectionRoyaltyWriter(entityManagerFactory)

    @Bean
    fun collectionRoyaltyAsyncWriter() = CollectionRoyaltyAsyncWriter(collectionRoyaltyWriter())

    @Bean
    fun collectionMetadataWriter() = CollectionMetadataWriter(entityManagerFactory)

    @Bean
    fun collectionMetadataAsyncWriter() = CollectionMetadataAsyncWriter(collectionMetadataWriter())

    @Bean
    fun itemInfoWriter() = ItemInfoWriter(entityManagerFactory)

    @Bean
    fun itemInfoAsyncWriter() = ItemInfoAsyncWriter(itemInfoWriter())

    @Bean
    fun itemInfoListWriter() = ListWriter<ItemInfo>(itemInfoWriter())

    @Bean
    fun itemInfoAsyncListWriter() = AsyncListWriter(itemInfoListWriter())

    @Bean
    fun itemRoyaltyWriter() = ItemRoyaltyWriter(entityManagerFactory)

    @Bean
    fun itemRoyaltyAsyncWriter() = ItemRoyaltyAsyncWriter(itemRoyaltyWriter())

    @Bean
    fun itemMetadataWriter() = ItemMetadataWriter(entityManagerFactory)

    @Bean
    fun itemMetadataAsyncWriter() = ItemMetadataAsyncWriter(itemMetadataWriter())

    @Bean
    fun itemSaleWriter() = ItemSaleWriter(entityManagerFactory)

    @Bean
    fun itemSaleAsyncWriter() = ItemSaleAsyncWriter(itemSaleWriter())
}
