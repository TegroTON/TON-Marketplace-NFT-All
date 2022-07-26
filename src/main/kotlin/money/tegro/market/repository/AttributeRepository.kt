package money.tegro.market.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.model.AttributeModel
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class AttributeRepository : ReactorPageableRepository<AttributeModel, Long> {
    abstract fun findByItem(item: AddrStd): Flux<AttributeModel>
    abstract fun findByItemAndTrait(item: AddrStd, trait: String): Mono<AttributeModel>
}
