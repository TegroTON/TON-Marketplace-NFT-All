package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class CollectionRepository : ReactorPageableRepository<CollectionModel, AddressKey> {
    fun findById(address: AddrStd) = findById(AddressKey.of(address))

    abstract fun existsByAddress(address: AddressKey): Mono<Boolean>
    fun existsByAddress(address: AddrStd) = existsByAddress(AddressKey.of(address))

    abstract fun persist(
        @Id address: AddressKey,
        updated: Instant = Instant.MIN,
        metadataUpdated: Instant = Instant.MIN,
        discovered: Instant = Instant.now()
    ): Mono<CollectionModel>

    abstract fun update(
        @Id address: AddressKey,
        nextItemIndex: Long?,
        owner: AddressKey?,
        content: ByteArray?,
        updated: Instant = Instant.now(),
    )

    abstract fun update(
        @Id address: AddressKey,
        name: String?,
        description: String?,
        image: String?,
        imageData: ByteArray?,
        coverImage: String?,
        coverImageData: ByteArray?,
        metadataUpdated: Instant = Instant.now()
    )
}
