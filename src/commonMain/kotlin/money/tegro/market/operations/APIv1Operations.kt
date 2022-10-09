package money.tegro.market.operations

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ItemDTO
import money.tegro.market.dto.TransactionRequestDTO

interface APIv1Operations {
    fun listTopCollections(drop: Int?, take: Int?): Flow<CollectionDTO>

    suspend fun getCollection(collection: String): CollectionDTO

    fun listCollectionItems(collection: String, drop: Int?, take: Int?): Flow<ItemDTO>

    suspend fun getItem(item: String): ItemDTO

    suspend fun transferItem(
        item: String,
        newOwner: String,
        responseDestination: String?,
    ): TransactionRequestDTO

    suspend fun sellItem(
        item: String,
        seller: String,
        price: BigInteger,
    ): TransactionRequestDTO
}
