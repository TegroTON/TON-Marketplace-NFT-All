package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface CollectionMetadataRepository : JpaRepository<CollectionMetadata, Long> {
    fun findByCollection(collection: CollectionInfo): CollectionMetadata?
}
