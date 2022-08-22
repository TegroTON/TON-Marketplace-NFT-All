package money.tegro.market.graphql

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.service.CollectionService
import money.tegro.market.service.RoyaltyService
import money.tegro.market.toRaw
import org.springframework.stereotype.Component
import org.ton.block.MsgAddressInt

@Component
class CollectionsQuery(
    private val collectionService: CollectionService,
    private val royaltyService: RoyaltyService,
) : Query {
    suspend fun allCollections() =
        collectionService.all()
            .map { it.toRaw() }
            .toList()

    suspend fun collection(
        address: String
    ) =
        CollectionDTO(
            MsgAddressInt(address),
            requireNotNull(collectionService.getContract(MsgAddressInt(address))) { "Unable to fetch collection contract" },
            requireNotNull(collectionService.getMetadata(MsgAddressInt(address))) { "Unable to parse collection metadata" },
            royaltyService.get(MsgAddressInt(address))
        )

    suspend fun collectionItemAddress(
        collection: String,
        index: String,
    ) =
        collectionService.getItemAddress(MsgAddressInt(collection), index.toULong()).toRaw()
}
