package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionInfo
import javax.persistence.EntityManagerFactory

class CollectionInfoWriter(private val entityManagerFactory: EntityManagerFactory) : EntityWriter<CollectionInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}
