package money.tegro.market.query

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.service.CollectionService
import org.springframework.stereotype.Component
import org.ton.block.MsgAddressInt

@Component
class RootQuery(
    private val collectionService: CollectionService,
) : Query {
    suspend fun collections(
        drop: Int? = null,
        take: Int? = null,
    ) =
        collectionService.listAll()
            .dropTake(drop, take)
            .map { CollectionQuery(it) }
            .toList()

    suspend fun collection(address: String) =
        CollectionQuery(MsgAddressInt(address))

    suspend fun item(address: String) =
        ItemQuery(MsgAddressInt(address))
}
