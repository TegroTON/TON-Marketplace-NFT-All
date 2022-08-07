package money.tegro.market.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.AccountModel
import org.ton.block.MsgAddressInt

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface AccountRepository : CoroutinePageableCrudRepository<AccountModel, MsgAddressInt>
