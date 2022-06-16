package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemMetadata
import javax.persistence.EntityManagerFactory

class ItemMetadataWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<ItemMetadata>() {
    init {
        configure(this, entityManagerFactory)
    }
}
