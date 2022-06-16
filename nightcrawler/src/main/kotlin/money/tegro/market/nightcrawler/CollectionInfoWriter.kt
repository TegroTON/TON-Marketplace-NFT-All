package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

class CollectionInfoWriter(private val entityManagerFactory: EntityManagerFactory) : EntityWriter<CollectionInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}

@Configuration
class CollectionInfoWriterConfiguration(private val entityManagerFactory: EntityManagerFactory) {
    @Bean
    fun collectionInfoWriter() = CollectionInfoWriter(entityManagerFactory)
}
