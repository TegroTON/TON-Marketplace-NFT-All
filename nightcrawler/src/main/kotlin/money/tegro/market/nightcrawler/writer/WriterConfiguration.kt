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
    fun itemInfoListWriter() = ListWriter<ItemInfo>(itemInfoWriter())
    
    @Bean
    fun itemInfoAsyncListWriter() = AsyncListWriter(itemInfoListWriter())
}
