package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTDeployedCollection
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.ReferenceBlock
import org.reactivestreams.Publisher
import org.ton.lite.api.LiteApi
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux

open class MissingItemProcessor<RB : ReferenceBlock>(
    private val itemRepository: ItemRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: RB,
) : java.util.function.Function<CollectionModel, Publisher<ItemModel>> {
    override fun apply(it: CollectionModel): Publisher<ItemModel> =
        (0 until (it.nextItemIndex ?: 0))
            .toFlux()
            .filterWhen { index ->
                // Ignore items that are already added and indexed
                itemRepository.existsByIndexAndCollection(index, it.address).map { !it }
            }
            .map { index -> it.address.to() to index }
            .publishOn(Schedulers.boundedElastic())
            .concatMap {
                mono { NFTDeployedCollection.itemAddressOf(it.first, it.second, liteApi, referenceBlock()) }
            }
            .filterWhen { itemRepository.existsByAddress(it).map { !it } }
            .map { ItemModel(it) }
}

@Prototype
class FixedMissingItemProcess(itemRepository: ItemRepository, liteApi: LiteApi, referenceBlock: FixedReferenceBlock) :
    MissingItemProcessor<FixedReferenceBlock>(
        itemRepository, liteApi, referenceBlock
    )

@Prototype
class LatestMissingItemProcess(
    itemRepository: ItemRepository,
    liteApi: LiteApi,
    referenceBlock: LatestReferenceBlock
) :
    MissingItemProcessor<LatestReferenceBlock>(
        itemRepository, liteApi, referenceBlock
    )
