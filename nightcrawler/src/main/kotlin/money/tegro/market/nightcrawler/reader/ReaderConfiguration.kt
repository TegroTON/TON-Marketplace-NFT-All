package money.tegro.market.nightcrawler.reader

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class ReaderConfiguration(private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    fun collectionInfoReader() = CollectionInfoReader(entityManagerFactory)
}
