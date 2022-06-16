package money.tegro.market.nightcrawler.reader

import money.tegro.market.nightcrawler.CollectionInfoReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class ReaderConfiguration(private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    fun collectionInfoReader() = CollectionInfoReader(entityManagerFactory)
}
