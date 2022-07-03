package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.H2)
abstract class CollectionRepository : ReactorPageableRepository<CollectionModel, AddressKey> {
    abstract fun findByIdForUpdate(id: AddressKey): Mono<CollectionModel>
    fun findById(address: AddrStd) = findById(AddressKey.of(address))


    abstract fun existsByAddress(address: AddressKey): Mono<Boolean>
    fun existsByAddress(address: AddrStd) = existsByAddress(AddressKey.of(address))

    abstract fun findByOwner(owner: AddressKey): Flux<CollectionModel>
    fun findByOwner(owner: AddrStd) = findByOwner(AddressKey.of(owner))

    @Transactional
    fun upsert(collection: CollectionModel) =
        findByIdForUpdate(collection.address)
            .doOnSuccess {
                if (it == null) {
                    save(collection)
                } else {
                    update(collection)
                }
            }
}
