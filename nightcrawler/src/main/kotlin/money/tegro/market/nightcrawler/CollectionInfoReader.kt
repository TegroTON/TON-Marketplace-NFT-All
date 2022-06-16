package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

class CollectionInfoReader(private val entityManagerFactory: EntityManagerFactory) : EntityReader<CollectionInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}

@Configuration
class CollectionInfoReaderConfiguration(private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    fun collectionInfoReader() = CollectionInfoReader(entityManagerFactory)
}
