package money.tegro.market.core.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel

@R2dbcRepository(dialect = Dialect.H2)
abstract class SaleRepository : ReactorPageableRepository<SaleModel, AddressKey>,
    BasicRepository<SaleModel> {
}

