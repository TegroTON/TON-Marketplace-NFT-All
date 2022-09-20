package money.tegro.market.service.item

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.service.collection.CollectionItemListService
import money.tegro.market.service.collection.CollectionListService
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ItemListService(
    private val collectionListService: CollectionListService,
    private val collectionItemListService: CollectionItemListService,
    private val itemContractService: ItemContractService,
    private val approvalRepository: ApprovalRepository,
) {
    @OptIn(FlowPreview::class)
    fun get() =
        merge(
            // Collection items
            collectionListService.get()
                .flatMapConcat { collectionItemListService.get(it).mapNotNull { it as? MsgAddressInt } },
            // Standalone items
            approvalRepository.findAllByApprovedIsTrue()
                .map { it.address }
                .filter { itemContractService.get(it) != null }
        )
}
