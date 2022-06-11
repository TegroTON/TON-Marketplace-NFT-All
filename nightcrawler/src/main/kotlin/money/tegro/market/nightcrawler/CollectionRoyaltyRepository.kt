package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionRoyalty
import org.springframework.data.jpa.repository.JpaRepository

interface CollectionRoyaltyRepository : JpaRepository<CollectionRoyalty, Long> {
    fun findByCollection(collection: CollectionInfo): CollectionRoyalty?
}
