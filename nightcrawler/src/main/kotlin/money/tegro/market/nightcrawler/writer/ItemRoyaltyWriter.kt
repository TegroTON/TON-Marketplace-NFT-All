package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemRoyalty
import javax.persistence.EntityManagerFactory

class ItemRoyaltyWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<ItemRoyalty>() {
    init {
        configure(this, entityManagerFactory)
    }
}
