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
}
