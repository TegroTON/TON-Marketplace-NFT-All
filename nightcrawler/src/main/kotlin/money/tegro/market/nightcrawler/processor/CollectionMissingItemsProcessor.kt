package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.nft.NFTDeployedCollection
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.lite.api.LiteApi

class CollectionMissingItemsProcessor(
    private val liteApi: LiteApi,
    private val itemInfoRepository: ItemInfoRepository,
) :
    AsyncItemProcessor<CollectionInfo, List<ItemInfo>>() {
    init {
        setDelegate { collection ->
            runBlocking {
                val addedIndices = collection.items.orEmpty().map { it.index }.filterNotNull()
                collection.nextItemIndex?.let { (0 until it) }
                    ?.filter { !addedIndices.contains(it) }
                    ?.map {
                        it to NFTDeployedCollection.itemAddressOf(collection.addressStd(), it, liteApi)
                    }
                    ?.map {
                        val (index, address) = it
                        (itemInfoRepository.findByAddress(address) ?: ItemInfo(address)).apply {
                            this.collection = collection
                            this.index = index
                        }
                    }
            }
        }
    }
}
