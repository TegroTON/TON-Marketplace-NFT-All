package money.tegro.market.core.repository

import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.model.ItemModel
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class ItemRepository : ReactorPageableRepository<ItemModel, AddrStd> {
    abstract fun findByIdForUpdate(id: AddrStd): Mono<ItemModel>

    abstract fun existsByAddress(address: AddrStd): Mono<Boolean>

    abstract fun existsByIndexAndCollection(index: Long, collection: MsgAddress): Mono<Boolean>

    abstract fun countByCollection(collection: MsgAddress): Mono<Long>
    abstract fun findByCollection(collection: MsgAddress, pageable: Pageable): Mono<Page<ItemModel>>

    abstract fun findByOwner(owner: MsgAddress): Flux<ItemModel>
}

