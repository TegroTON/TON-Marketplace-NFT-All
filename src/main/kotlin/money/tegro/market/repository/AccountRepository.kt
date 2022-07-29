package money.tegro.market.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.AccountModel
import org.ton.block.AddrStd

@JdbcRepository(dialect = Dialect.POSTGRES)
interface AccountRepository : CoroutinePageableCrudRepository<AccountModel, AddrStd>
