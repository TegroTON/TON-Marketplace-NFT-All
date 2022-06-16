package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import money.tegro.market.nightcrawler.reader.EntityReader
import javax.persistence.EntityManagerFactory

class CollectionInfoReader(private val entityManagerFactory: EntityManagerFactory) : EntityReader<CollectionInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}
