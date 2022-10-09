package money.tegro.market.operations

import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ItemAddressDTO
import money.tegro.market.dto.TopCollectionDTO

interface CollectionOperations {
    fun listTopCollections(): Flow<TopCollectionDTO>

    suspend fun getByAddress(address: String): CollectionDTO

    fun listCollectionItems(address: String, drop: Int? = null, take: Int? = null): Flow<ItemAddressDTO>
}
