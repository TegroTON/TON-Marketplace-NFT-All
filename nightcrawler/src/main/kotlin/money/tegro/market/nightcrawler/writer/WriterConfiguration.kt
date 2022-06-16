package money.tegro.market.nightcrawler.writer

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
}
