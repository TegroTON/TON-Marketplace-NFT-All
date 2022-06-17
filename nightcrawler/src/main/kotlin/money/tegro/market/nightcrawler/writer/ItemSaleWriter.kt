package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemSale
import javax.persistence.EntityManagerFactory

class ItemSaleWriter(entityManagerFactory: EntityManagerFactory) : EntityWriter<ItemSale>() {
    init {
        configure(this, entityManagerFactory)
    }
}
