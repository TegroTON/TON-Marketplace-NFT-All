package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemInfo
import javax.persistence.EntityManagerFactory

class ItemInfoWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<ItemInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}
