package money.tegro.market.service.collection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.stereotype.Service
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

@Service
class CollectionItemListService(
    private val collectionContractService: CollectionContractService,
    private val collectionItemAddressService: CollectionItemAddressService,
) {
    suspend fun get(address: MsgAddressInt): Flow<MsgAddress> {
        return (0uL until (collectionContractService.get(address)?.next_item_index ?: 0uL))
            .asFlow()
            .mapNotNull { collectionItemAddressService.get(address, it) }
    }
}
