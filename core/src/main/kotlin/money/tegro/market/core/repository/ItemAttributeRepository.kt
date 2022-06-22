package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemAttributeModel
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemAttributeRepository : ReactorPageableRepository<ItemAttributeModel, Long> {
    abstract fun findByItemAndTrait(item: AddressKey, trait: String): Mono<ItemAttributeModel>

    abstract fun update(
        @Id id: Long,
        value: String,
    ): Unit
}

