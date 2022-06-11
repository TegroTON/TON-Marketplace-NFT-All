package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface CollectionRoyaltyRepository : JpaRepository<CollectionRoyalty, Long> {
    fun findByCollection(collection: CollectionInfo): CollectionRoyalty?
}
