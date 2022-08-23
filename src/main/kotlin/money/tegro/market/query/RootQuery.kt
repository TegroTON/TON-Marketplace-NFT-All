package money.tegro.market.query

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.service.CollectionService
import org.springframework.stereotype.Component
import org.ton.block.MsgAddressInt

@Component
class RootQuery(
    private val collectionService: CollectionService,
) : Query {
    suspend fun collections() =
        collectionService.all()
            .map { CollectionQuery(it) }
            .toList()

    suspend fun collection(address: String) =
        CollectionQuery(MsgAddressInt(address))

    suspend fun item(address: String) =
        ItemQuery(MsgAddressInt(address))
}
