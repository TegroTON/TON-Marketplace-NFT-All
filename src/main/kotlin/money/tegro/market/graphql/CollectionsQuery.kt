package money.tegro.market.graphql

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.data.CollectionData
import money.tegro.market.service.CollectionService
import money.tegro.market.service.RoyaltyService
import org.springframework.stereotype.Component

@Component
class CollectionsQuery(
    private val collectionService: CollectionService,
    private val royaltyService: RoyaltyService,
) : Query {
    suspend fun collections() =
        collectionService.all()
            .map { CollectionData(it) }
            .toList()
}
