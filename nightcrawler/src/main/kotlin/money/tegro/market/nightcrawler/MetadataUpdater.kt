package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import java.time.Instant

open class MetadataUpdater<M : MetadataModel> : java.util.function.Function<M, Publisher<M>> {
    override fun apply(it: M): Publisher<M> =
        mono {
            it.content?.let { content ->
                val nftMetadata = NFTMetadata.of(BagOfCells(content).roots.first())

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
class ItemMetadataUpdater : MetadataUpdater<ItemModel>()
