package money.tegro.market.service.collection

import com.sksamuel.aedile.core.caffeineBuilder
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.reduce
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
        caffeineBuilder<MsgAddressInt, ULong>().build()

    suspend fun get(address: MsgAddressInt): ULong =
        cache.getOrPut(address) { collection ->
            collectionItemListService.get(collection)
                .mapNotNull { item ->
                    (item as? MsgAddressInt)?.let { itemOwnerService.get(it) }?.let { mutableMapOf(it to 0uL) }
                }
                .reduce { accumulator, value ->
                    accumulator.merge(value.keys.first(), 1uL) { t, u -> t + u }
                    accumulator
                }
                .values
                .sum()
        }

    companion object : KLogging()
}
