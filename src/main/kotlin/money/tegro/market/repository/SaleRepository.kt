package money.tegro.market.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.model.SaleModel
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface SaleRepository : CoroutinePageableCrudRepository<SaleModel, MsgAddressInt> {
    @Query(
        """
        INSERT INTO attributes(address, numerator, denominator, destination, updated)
        VALUES (:address, :numerator, :denominator, :destination, :updated)
        ON CONFLICT(address) DO UPDATE SET
           numerator = EXCLUDED.numerator,
           denominator = EXCLUDED.denominator,
           destination = EXCLUDED.destination,
           updated = EXCLUDED.updated
        RETURNING *
        """
    )
    suspend fun upsert(
        @Id address: MsgAddressInt,
        numerator: Int,
        denominator: Int,
        destination: MsgAddress,
        updated: Instant = Instant.now()
    ): RoyaltyModel
}
