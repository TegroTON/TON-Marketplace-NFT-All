package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.RoyaltyModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class RoyaltyRepository : ReactorPageableRepository<RoyaltyModel, AddressKey> {
    fun findById(address: AddrStd) = findById(AddressKey.of(address))
    abstract fun existsByAddress(address: AddressKey): Mono<Boolean>
    fun existsByAddress(address: AddrStd) = existsByAddress(AddressKey.of(address))

    abstract fun findByIdForUpdate(id: AddressKey): Mono<RoyaltyModel>


    @Transactional
    fun upsert(it: RoyaltyModel) = mono {
        (findByIdForUpdate(it.address).awaitSingleOrNull()?.let { old ->
            update(it)
        } ?: run {
            save(it)
        }).then().awaitSingleOrNull()
    }
}
