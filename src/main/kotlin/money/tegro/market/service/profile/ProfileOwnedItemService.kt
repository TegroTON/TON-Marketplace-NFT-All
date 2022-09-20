package money.tegro.market.service.profile

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import money.tegro.market.service.item.ItemListService
import money.tegro.market.service.item.ItemOwnerService
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ProfileOwnedItemService(
    private val itemListService: ItemListService,
    private val itemOwnerService: ItemOwnerService,
) {
    fun get(address: MsgAddressInt): Flow<MsgAddressInt> =
        itemListService.get()
            .filter { itemOwnerService.get(it) == address }
}
