package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class SaleRepository : ReactorPageableRepository<SaleModel, AddressKey> {
    fun findById(address: AddrStd) = findById(AddressKey.of(address))
    abstract fun findByItem(item: AddressKey): Mono<SaleModel>
    abstract fun findByOwner(owner: AddressKey): Mono<SaleModel>
    fun findByOwner(owner: AddrStd) = findByOwner(AddressKey.of(owner))

    abstract fun findByIdForUpdate(id: AddressKey): Mono<SaleModel>

    @Transactional
    fun upsert(it: SaleModel) = mono {
        (findByIdForUpdate(it.address).awaitSingleOrNull()?.let { old ->
            update(it)
        } ?: run {
            save(it)
        }).then().awaitSingleOrNull()
    }
}

