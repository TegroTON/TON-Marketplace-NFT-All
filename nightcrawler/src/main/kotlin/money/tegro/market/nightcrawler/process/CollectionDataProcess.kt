package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.ReferenceBlock
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

open class CollectionDataProcess<RB : ReferenceBlock>(
    private val liteApi: LiteApi,
    private val referenceBlock: RB,
) : java.util.function.Function<CollectionModel, Publisher<CollectionModel>> {
    override fun apply(it: CollectionModel): Publisher<CollectionModel> = mono {
        val collection = NFTCollection.of(it.address.to(), liteApi, referenceBlock())

        it.apply {
            nextItemIndex = collection.nextItemIndex
            content = BagOfCells(collection.content).toByteArray()
            owner = AddressKey.of(collection.owner)

            updated = Instant.now()
        }
    }
}

@Prototype
class FixedCollectionDataProcess(
    liteApi: LiteApi,
    referenceBlock: FixedReferenceBlock
) : CollectionDataProcess<FixedReferenceBlock>(liteApi, referenceBlock)


@Prototype
class LatestCollectionDataProcess(
    liteApi: LiteApi,
    referenceBlock: LatestReferenceBlock
) : CollectionDataProcess<LatestReferenceBlock>(liteApi, referenceBlock)


