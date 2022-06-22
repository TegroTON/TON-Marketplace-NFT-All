package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

open class MetadataFetcher<M : MetadataModel> :
    java.util.function.Function<M, Publisher<Pair<M, NFTMetadata>>> {
    open fun getContent(it: M): Mono<Cell> = it.content?.let { BagOfCells(it).roots.first() }.toMono()

    override fun apply(it: M): Publisher<Pair<M, NFTMetadata>> =
        getContent(it)
            .flatMap { content ->
                mono { it to NFTMetadata.of(content) }
            }
}

@Singleton
class CollectionMetadataFetcher : MetadataFetcher<CollectionModel>()

@Singleton
class ItemMetadataFetcher(private val liteApi: LiteApi) : MetadataFetcher<ItemModel>() {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun getContent(it: ItemModel): Mono<Cell> = mono {
        it.collection?.to()?.let { collection ->
            it.index?.let { index ->
                it.content?.let { BagOfCells(it).roots.first() }?.let { individualContent ->
                    NFTDeployedCollectionItem.contentOf(collection, index, individualContent, liteApi)
                }
            }
        } ?: it.content?.let { BagOfCells(it).roots.first() }
    }
}
