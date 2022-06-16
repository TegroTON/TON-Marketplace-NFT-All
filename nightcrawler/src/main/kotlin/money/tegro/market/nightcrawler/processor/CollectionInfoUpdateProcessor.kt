package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfo
import money.tegro.market.nft.NFTCollection
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

class CollectionInfoUpdateProcessor(
    private val liteApi: LiteApi
) :
    AsyncItemProcessor<CollectionInfo, CollectionInfo>() {
    init {
        setDelegate {
            runBlocking {
                val collection = NFTCollection.of(it.addressStd(), liteApi)

                it.updated = Instant.now()
                it.cneq(it::nextItemIndex, collection.nextItemIndex)
                it.cneq(it::ownerWorkchain, collection.owner.workchainId)
                it.cneq(it::ownerAddress, collection.owner.address.toByteArray())
                it.cneq(it::content, BagOfCells(collection.content).toByteArray())

                it
            }
        }
    }
}
