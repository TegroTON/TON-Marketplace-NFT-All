package money.tegro.market.service.collection

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.stereotype.Service
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class CollectionItemListService(
    private val collectionContractService: CollectionContractService,
    private val collectionItemAddressService: CollectionItemAddressService,
    private val liteClient: LiteClient,
) {
    suspend fun get(
        address: MsgAddressInt,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getLastBlockId() }
    ): Flow<MsgAddress> {
        return (0uL until (collectionContractService.get(address, referenceBlock)?.next_item_index ?: 0uL))
            .asFlow()
            .mapNotNull { collectionItemAddressService.get(address, it, referenceBlock) }
    }
}
