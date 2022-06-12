package money.tegro.market.nightcrawler

import money.tegro.market.db.*
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class ItemReaderConfiguration(val entityManagerFactory: EntityManagerFactory) {
    private inline fun <reified T> entityReader() = JpaCursorItemReaderBuilder<T>()
        .name(T::class.java.simpleName + "Reader")
        .queryString("from " + T::class.java.simpleName)
        .entityManagerFactory(entityManagerFactory)
        .saveState(false)
        .build()

    @Bean
    fun collectionInfoReader() = entityReader<CollectionInfo>()

    @Bean
    fun collectionRoyaltyReader() = entityReader<CollectionRoyalty>()

    @Bean
    fun collectionMetadataReader() = entityReader<CollectionMetadata>()

    @Bean
    fun itemInfoReader() = entityReader<ItemInfo>()

    @Bean
    fun itemRoyaltyReader() = entityReader<ItemRoyalty>()

    @Bean
    fun itemMetadataReader() = entityReader<ItemMetadata>()

    @Bean
    fun itemSaleReader() = entityReader<ItemSale>()
}
