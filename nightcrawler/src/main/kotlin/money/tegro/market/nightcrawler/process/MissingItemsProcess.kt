package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.dto.toKey
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.bool.not

@Prototype
class MissingItemsProcess(
    private val itemRepository: ItemRepository,
    private val liteApi: LiteApi,
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { collection: CollectionModel ->
            (0 until (collection.nextItemIndex ?: 0))
                .toFlux()
                .publishOn(Schedulers.boundedElastic())
                .filterWhen { index ->
                    // Ignore items that are already added and indexed
                    itemRepository.existsByIndexAndCollection(index, collection.address).not()
                }
                .concatMap { index ->
                    mono {
                        logger.debug(
                            "fetching missing collection {} item no. {}",
                            value("collection", collection.address.toSafeBounceable()),
                            value("index", index)
                        )
                        NFTCollection.itemAddressOf(
                            collection.address.to(),
                            index,
                            liteApi,
                            referenceBlock
                        ) as? AddrStd // If not addrstd, this is null and we just skip it
                    }
                }
                .filterWhen { itemRepository.existsById(it.toKey()).not() } // Isn't in the repository?
                .concatMap {
                    mono {
                        logger.debug("getting missing item {} information", value("address", it.toSafeBounceable()))
                        val item = NFTItem.of(it, liteApi, referenceBlock)
                        ItemModel.of(item, item.metadata(liteApi, referenceBlock))
                    }
                }
        }

    companion object : KLogging()
}
