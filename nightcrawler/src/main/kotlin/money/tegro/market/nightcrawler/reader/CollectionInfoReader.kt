package money.tegro.market.nightcrawler.reader

import money.tegro.market.db.CollectionInfo
import javax.persistence.EntityManagerFactory

class CollectionInfoReader(entityManagerFactory: EntityManagerFactory) : EntityReader<CollectionInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}
