package money.tegro.market.nightcrawler

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import money.tegro.market.core.model.CollectionData
import money.tegro.market.core.model.ItemData
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemRepository : ReactorCrudRepository<ItemData, Long> {
    abstract fun existsByWorkchainAndAddress(workchain: Int, address: ByteArray): Boolean

    abstract fun existsByIndexAndCollection(index: Long, collection: CollectionData): Boolean

    fun existsByAddressStd(address: MsgAddressIntStd) =
        existsByWorkchainAndAddress(address.workchainId, address.address.toByteArray())

    abstract fun update(
        @Id id: Long,
        index: Long?,
        collection: CollectionData?,
        ownerWorkchain: Int?,
        ownerAddress: ByteArray?,
        content: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit

    abstract fun update(
        @Id id: Long,
        name: String?,
        description: String?,
        image: String?,
        imageData: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit
    
    abstract fun update(
        @Id id: Long,
        numerator: Int?,
        denominator: Int?,
        destinationWorkchain: Int?,
        destinationAddress: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit
}
