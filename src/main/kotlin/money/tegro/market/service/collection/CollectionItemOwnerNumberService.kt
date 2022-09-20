package money.tegro.market.service.collection

import com.sksamuel.aedile.core.caffeineBuilder
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.service.item.ItemOwnerService
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class CollectionItemOwnerNumberService(
    private val collectionItemListService: CollectionItemListService,
    private val itemOwnerService: ItemOwnerService,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, ULong> {
            // TODO: Configuration
        }
            .build()

    suspend fun get(address: MsgAddressInt): ULong =
        cache.getOrPut(address) { collection ->
            collectionItemListService.get(collection)
                .map { item -> (item as? MsgAddressInt)?.let { itemOwnerService.get(it) } }
                .distinctUntilChanged()
                .toList()
                .distinct()
                .size
                .toULong() // TODO
        }

    companion object : KLogging()
}
