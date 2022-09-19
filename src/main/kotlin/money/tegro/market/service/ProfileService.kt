package money.tegro.market.service

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ProfileService(
    private val collectionService: CollectionService,
    private val itemService: ItemService,
) {
    @OptIn(FlowPreview::class)
    fun listItemsOf(address: MsgAddressInt): Flow<MsgAddressInt> =
        merge(
            collectionService.listAll()
                .flatMapConcat { collectionService.listItemAddresses(it).mapNotNull { it as? MsgAddressInt } },
            itemService.listAll()
        )
            .filter { itemService.getContract(it)?.owner == address }

    fun listCollectionsOf(address: MsgAddressInt): Flow<MsgAddressInt> =
        collectionService.listAll()
            .filter { collectionService.getContract(it)?.owner == address }

    companion object : KLogging()
}
