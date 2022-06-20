package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemRepository : ReactorCrudRepository<ItemModel, Long>,
    BasicRepository<ItemModel>, MetadataRepository, RoyaltyRepository {
    abstract fun existsByIndexAndCollection(index: Long, collection: CollectionModel): Boolean

    abstract fun update(
        @Id id: Long,
        initialized: Boolean,
        index: Long?,
        collection: CollectionModel?,
        ownerWorkchain: Int?,
        ownerAddress: ByteArray?,
        content: ByteArray?,
        dataModified: Instant? = Instant.now(),
        dataUpdated: Instant? = Instant.now(),
    ): Unit
}

