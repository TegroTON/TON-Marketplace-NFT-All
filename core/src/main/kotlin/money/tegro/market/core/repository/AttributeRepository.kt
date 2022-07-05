package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.AttributeModel
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class AttributeRepository : ReactorPageableRepository<AttributeModel, Long> {
    abstract fun findByItem(item: AddressKey): Flux<AttributeModel>
    abstract fun findByItemAndTraitForUpdate(item: AddressKey, trait: String): Mono<AttributeModel>

    @Transactional
    fun upsert(it: AttributeModel) = mono {
        (findByItemAndTraitForUpdate(it.item, it.trait).awaitSingleOrNull()?.let { old ->
            update(it.apply { id = old.id })
        } ?: run {
            save(it)
        }).then().awaitSingleOrNull()
    }
}

