package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.findByAddress
import money.tegro.market.nft.NFTDeployedCollectionItem
import money.tegro.market.nft.NFTItem
import money.tegro.market.ton.LiteApiFactory
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.boc.BagOfCells
import java.time.Instant

class ItemInfoUpdateProcessor(
    private val liteApiFactory: LiteApiFactory,
    private val collectionInfoRepository: CollectionInfoRepository,
) : AsyncItemProcessor<ItemInfo, ItemInfo>() {
    init {
        setDelegate {
            runBlocking {
                val item = NFTItem.of(it.addressStd(), liteApiFactory.getObject())

                it.apply {
                    updated = Instant.now()
                    cneq(it::initialized, item != null)
                    cneq(it::index, item?.index)
                    cneq(it::ownerWorkchain, item?.owner?.workchainId)
                    cneq(it::ownerAddress, item?.owner?.address?.toByteArray())
                    cneq(it::content, item?.individualContent?.let { BagOfCells(it) }?.toByteArray())
                    cneq(
                        it::collection,
                        (item as? NFTDeployedCollectionItem)?.collection?.let {
                            collectionInfoRepository.findByAddress(it)
                        })
                }
            }
        }
    }
}
