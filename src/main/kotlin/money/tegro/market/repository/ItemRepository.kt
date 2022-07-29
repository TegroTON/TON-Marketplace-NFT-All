package money.tegro.market.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.core.model.ItemModel
import org.ton.block.AddrStd
import org.ton.block.MsgAddress

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ItemRepository : CoroutinePageableCrudRepository<ItemModel, AddrStd> {
    suspend fun existsByCollectionAndIndex(collection: MsgAddress, index: Long): Boolean
    suspend fun existsByOwner(owner: MsgAddress): Boolean
}

