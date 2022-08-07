package money.tegro.market.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.CollectionModel
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface CollectionRepository : CoroutinePageableCrudRepository<CollectionModel, MsgAddressInt> {
    suspend fun existsByOwner(owner: MsgAddress): Boolean
}
