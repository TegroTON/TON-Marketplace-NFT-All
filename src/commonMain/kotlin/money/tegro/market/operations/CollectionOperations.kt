package money.tegro.market.operations

import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.TopCollectionDTO

interface CollectionOperations {
    fun listTopCollections(): Flow<TopCollectionDTO>

    suspend fun getByAddress(address: String): CollectionDTO
}
