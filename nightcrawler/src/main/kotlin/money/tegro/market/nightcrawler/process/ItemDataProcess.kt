package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.ReferenceBlock
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

open class ItemDataProcess<RB : ReferenceBlock>(
    private val liteApi: LiteApi,
    private val referenceBlock: RB,
) : java.util.function.Function<ItemModel, Publisher<ItemModel>> {
    override fun apply(it: ItemModel): Publisher<ItemModel> = mono {
        val item = NFTItem.of(it.address.to(), liteApi, referenceBlock())

        it.apply {
            initialized = item != null
            index = item?.index
            collection = (item as? NFTDeployedCollectionItem)?.collection?.let { AddressKey.of(it) }
            owner = item?.owner?.let { AddressKey.of(it) }
            content = item?.content(liteApi, referenceBlock())?.let { BagOfCells(it).toByteArray() }
            updated = Instant.now()
        }
    }
}

@Prototype
class FixedItemDataProcess(
    liteApi: LiteApi,
    referenceBlock: FixedReferenceBlock
) : ItemDataProcess<FixedReferenceBlock>(liteApi, referenceBlock)

@Prototype
class LatestItemDataProcess(
    liteApi: LiteApi,
    referenceBlock: LatestReferenceBlock
) : ItemDataProcess<LatestReferenceBlock>(liteApi, referenceBlock)
