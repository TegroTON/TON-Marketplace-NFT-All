package money.tegro.market.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.model.SaleModel
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class SaleRepository : ReactorPageableRepository<SaleModel, AddrStd> {
    abstract fun findByItem(item: MsgAddress): Mono<SaleModel>
    abstract fun findByOwner(owner: MsgAddress): Flux<SaleModel>
    abstract fun findByIdForUpdate(id: AddrStd): Mono<SaleModel>
}

