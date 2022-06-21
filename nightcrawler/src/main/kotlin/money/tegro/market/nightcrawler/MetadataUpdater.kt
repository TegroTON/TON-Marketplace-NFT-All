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
import money.tegro.market.core.model.addressStd
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Instant

open class MetadataUpdater<M : MetadataModel> : java.util.function.Function<M, Publisher<M>> {
    open fun getContent(it: M): Mono<Cell> = it.content?.let { BagOfCells(it).roots.first() }.toMono()

    override fun apply(it: M): Publisher<M> =
        getContent(it)
            .flatMap { content ->
                mono {
                    val nftMetadata = NFTMetadata.of(content)

                    it.apply {
                        name = nftMetadata.name
                        description = nftMetadata.description
                        image = nftMetadata.image
                        imageData = nftMetadata.imageData
                        coverImage = nftMetadata.coverImage
                        coverImageData = nftMetadata.coverImageData
                        metadataModified = Instant.now() // TODO
                    }
                }
            }
}

@Singleton
class CollectionMetadataUpdater : MetadataUpdater<CollectionModel>()

@Singleton
class ItemMetadataUpdater(private val liteApi: LiteApi) : MetadataUpdater<ItemModel>() {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun getContent(it: ItemModel): Mono<Cell> = mono {
        it.collection?.addressStd()?.let { collection ->
            it.index?.let { index ->
                it.content?.let { BagOfCells(it).roots.first() }?.let { individualContent ->
                    NFTDeployedCollectionItem.contentOf(collection, index, individualContent, liteApi)
                }
            }
        } ?: it.content?.let { BagOfCells(it).roots.first() }
    }
}
