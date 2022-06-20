package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import money.tegro.market.core.model.CollectionModel
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class CollectionRepository : ReactorCrudRepository<CollectionModel, Long>,
    BasicRepository<CollectionModel>, MetadataRepository, RoyaltyRepository {
    abstract fun update(
        @Id id: Long,
        nextItemIndex: Long?,
        ownerWorkchain: Int?,
        ownerAddress: ByteArray?,
        content: ByteArray?,
        dataModified: Instant? = Instant.now(),
        dataUpdated: Instant? = Instant.now(),
    ): Unit
}
