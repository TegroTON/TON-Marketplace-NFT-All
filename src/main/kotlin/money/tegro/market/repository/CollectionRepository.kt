package money.tegro.market.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.model.CollectionModel
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.POSTGRES)
abstract class CollectionRepository : CoroutinePageableCrudRepository<CollectionModel, MsgAddressInt> {
    abstract suspend fun existsByOwner(owner: MsgAddress): Boolean

    abstract suspend fun insert(
        @Id address: MsgAddressInt,
        nextItemIndex: Long,
        owner: MsgAddress,
        name: String?,
        description: String?,
        image: String?,
        coverImage: String?,
        enabled: Boolean = false,
        updated: Instant = Instant.now()
    ): CollectionModel

    abstract suspend fun update(
        @Id address: MsgAddressInt,
        nextItemIndex: Long,
        owner: MsgAddress,
        name: String?,
        description: String?,
        image: String?,
        coverImage: String?,
        updated: Instant = Instant.now()
    ): Long

    @Transactional(Transactional.TxType.MANDATORY)
    suspend fun upsert(
        @Id address: MsgAddressInt,
        nextItemIndex: Long,
        owner: MsgAddress,
        name: String?,
        description: String?,
        image: String?,
        coverImage: String?,
        updated: Instant = Instant.now()
    ): Long =
        if (existsById(address)) {
            update(address, nextItemIndex, owner, name, description, image, coverImage, updated)
        } else {
            insert(address, nextItemIndex, owner, name, description, image, coverImage, updated = updated)
            1L
        }
}
