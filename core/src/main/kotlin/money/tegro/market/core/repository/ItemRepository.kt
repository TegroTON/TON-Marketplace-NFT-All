package money.tegro.market.core.repository

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemRepository : ReactorPageableRepository<ItemModel, AddressKey> {
    abstract fun findByIdForUpdate(id: AddressKey): Mono<ItemModel>
    fun findById(address: AddrStd) = findById(AddressKey.of(address))

    abstract fun existsByAddress(address: AddressKey): Mono<Boolean>
    fun existsByAddress(address: AddrStd) = existsByAddress(AddressKey.of(address))

    abstract fun existsByIndexAndCollection(index: Long, collection: AddressKey): Mono<Boolean>

    abstract fun countByCollection(collection: AddressKey): Mono<Long>
    abstract fun findByCollection(collection: AddressKey, pageable: Pageable): Mono<Page<ItemModel>>

    @Transactional
    fun upsert(it: ItemModel) = mono {
        (findByIdForUpdate(it.address).awaitSingleOrNull()?.let { old ->
            update(it)
        } ?: run {
            save(it)
        }).then().awaitSingleOrNull()
    }
}

