package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionRoyalty
import javax.persistence.EntityManagerFactory

class CollectionRoyaltyWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<CollectionRoyalty>() {
    init {
        configure(this, entityManagerFactory)
    }
}
