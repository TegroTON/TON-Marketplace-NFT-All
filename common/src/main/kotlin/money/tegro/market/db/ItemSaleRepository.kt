package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface ItemSaleRepository : JpaRepository<ItemSale, Long> {
    fun findByItem(item: ItemInfo): ItemSale?
}
