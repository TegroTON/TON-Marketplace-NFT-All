package money.tegro.market.nightcrawler

import money.tegro.market.db.*
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class ItemWriterConfiguration(val entityManagerFactory: EntityManagerFactory) {
    private inline fun <reified T> entityWriter() = JpaItemWriterBuilder<T>()
        .entityManagerFactory(entityManagerFactory)
        .build()

    @Bean
    fun collectionInfoWriter() = entityWriter<CollectionInfo>()

    @Bean
    fun collectionRoyaltyWriter() = entityWriter<CollectionRoyalty>()

    @Bean
    fun collectionMetadataWriter() = entityWriter<CollectionMetadata>()

    @Bean
    fun itemInfoWriter() = entityWriter<ItemInfo>()

    @Bean
    fun itemInfoListWriter() = ItemListWriter<ItemInfo>(entityWriter())

    @Bean
    fun itemRoyaltyWriter() = entityWriter<ItemRoyalty>()

    @Bean
    fun itemMetadataWriter() = entityWriter<ItemMetadata>()

    @Bean
    fun itemSaleWriter() = entityWriter<ItemSale>()
}
