package money.tegro.market.operations

import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.TopCollectionDTO

interface CollectionOperations {
    suspend fun listTopCollections(): Flow<TopCollectionDTO>
}
