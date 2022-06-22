package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import reactor.core.publisher.Mono
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemRepository : ReactorPageableRepository<ItemModel, AddressKey>,
    BasicRepository<ItemModel>, MetadataRepository, RoyaltyRepository {
    abstract fun existsByIndexAndCollection(index: Long, collection: AddressKey): Boolean

    abstract fun findByCollection(collection: AddressKey, pageable: Pageable): Mono<Page<ItemModel>>

    abstract fun update(
        @Id address: AddressKey,
        initialized: Boolean,
        index: Long?,
        collection: AddressKey?,
        owner: AddressKey?,
        content: ByteArray?,
        modified: Instant? = Instant.now(),
        updated: Instant = Instant.now(),
    ): Unit
}

