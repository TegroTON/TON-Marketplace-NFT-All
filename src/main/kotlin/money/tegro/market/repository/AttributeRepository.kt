package money.tegro.market.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.AttributeModel
import org.ton.block.AddrStd

@JdbcRepository(dialect = Dialect.POSTGRES)
interface AttributeRepository : CoroutinePageableCrudRepository<AttributeModel, Long> {
    @Query(
        "INSERT INTO attributes(item, trait, value) VALUES (:item, :trait, :value)" +
                " ON CONFLICT(item, trait) DO UPDATE SET value = EXCLUDED.value"
    )
    suspend fun upsert(item: AddrStd, trait: String, value: String): Long
}

