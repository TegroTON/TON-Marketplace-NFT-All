package money.tegro.market.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Query
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.market.core.model.ItemModel
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ItemRepository : CoroutinePageableCrudRepository<ItemModel, MsgAddressInt> {
    suspend fun existsByCollectionAndIndex(collection: MsgAddress, index: Long): Boolean
    suspend fun existsByOwner(owner: MsgAddress): Boolean

    @Query(
        """
        INSERT INTO items(address, initialized, index, collection, owner, name, description, image, attributes, updated)
        VALUES (:address, :initialized, :index, :collection, :owner, :name, :description, :image, :attributes, :updated)
        ON CONFLICT(address) DO UPDATE SET
            initialized = EXCLUDED.initialized,
            index = EXCLUDED.index,
            collection = EXCLUDED.collection,
            owner = EXCLUDED.owner,
            name = EXCLUDED.name,
            description = EXCLUDED.description,
            image = EXCLUDED.image,
            attributes = EXCLUDED.attributes,
            updated = EXCLUDED.updated
        RETURNING *
        """
    )
    suspend fun upsert(
        @Id address: MsgAddressInt,
        initialized: Boolean,
        index: Long,
        collection: MsgAddress,
        owner: MsgAddress,
        name: String?,
        description: String?,
        image: String?,
        attributes: Map<String, String> = mapOf(),
        updated: Instant = Instant.now()
    ): ItemModel
}

