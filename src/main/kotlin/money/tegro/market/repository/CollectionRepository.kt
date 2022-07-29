package money.tegro.market.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.CollectionModel
import org.ton.block.AddrStd
import org.ton.block.MsgAddress

@JdbcRepository(dialect = Dialect.POSTGRES)
interface CollectionRepository : CoroutinePageableCrudRepository<CollectionModel, AddrStd> {
    suspend fun existsByOwner(owner: MsgAddress): Boolean
}
