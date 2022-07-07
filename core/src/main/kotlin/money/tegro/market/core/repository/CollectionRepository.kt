package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.model.CollectionModel
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class CollectionRepository : ReactorPageableRepository<CollectionModel, AddrStd> {
    abstract fun findByIdForUpdate(id: AddrStd): Mono<CollectionModel>

    abstract fun existsByAddress(address: AddrStd): Mono<Boolean>

    abstract fun findByOwner(owner: MsgAddress): Flux<CollectionModel>
}
