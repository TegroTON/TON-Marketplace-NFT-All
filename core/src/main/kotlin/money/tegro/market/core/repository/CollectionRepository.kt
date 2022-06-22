package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class CollectionRepository : ReactorPageableRepository<CollectionModel, AddressKey>,
    BasicRepository<CollectionModel>, MetadataRepository, RoyaltyRepository {
    abstract fun update(
        @Id address: AddressKey,
        nextItemIndex: Long?,
        owner: AddressKey?,
        content: ByteArray?,
        modified: Instant = Instant.now(),
        updated: Instant = Instant.now(),
    ): Unit
}
