package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.model.AccountModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class AccountRepository : ReactorPageableRepository<AccountModel, AddrStd> {
    abstract fun findByIdForUpdate(id: AddrStd): Mono<AccountModel>
}

