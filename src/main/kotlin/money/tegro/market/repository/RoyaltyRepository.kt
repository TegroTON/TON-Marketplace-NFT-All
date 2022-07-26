package money.tegro.market.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.model.RoyaltyModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class RoyaltyRepository : ReactorPageableRepository<RoyaltyModel, AddrStd> {
    abstract fun existsByAddress(address: AddrStd): Mono<Boolean>

    abstract fun findByIdForUpdate(id: AddrStd): Mono<RoyaltyModel>
}
