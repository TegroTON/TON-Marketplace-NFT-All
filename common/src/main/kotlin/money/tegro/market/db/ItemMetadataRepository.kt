package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface ItemMetadataRepository : JpaRepository<ItemMetadata, Long> {
    fun findByItem(item: ItemInfo): ItemMetadata?
}
