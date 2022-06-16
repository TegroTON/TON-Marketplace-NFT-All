package money.tegro.market.nightcrawler.reader

import money.tegro.market.db.ItemInfo
import javax.persistence.EntityManagerFactory

class ItemInfoReader(entityManagerFactory: EntityManagerFactory) : EntityReader<ItemInfo>() {
    init {
        configure(this, entityManagerFactory)
    }
}
