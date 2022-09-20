package money.tegro.market.service.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import money.tegro.market.service.collection.CollectionContractService
import money.tegro.market.service.collection.CollectionListService
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ProfileOwnedCollectionService(
    private val collectionContractService: CollectionContractService,
    private val collectionListService: CollectionListService,
) {
    fun get(address: MsgAddressInt): Flow<MsgAddressInt> =
        collectionListService.get()
            .filter { collectionContractService.get(it)?.owner == address }
}
