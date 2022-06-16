package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionMetadata
import javax.persistence.EntityManagerFactory

class CollectionMetadataWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<CollectionMetadata>() {
    init {
        configure(this, entityManagerFactory)
    }
}
