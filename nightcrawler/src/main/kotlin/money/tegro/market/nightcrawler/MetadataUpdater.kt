package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import org.reactivestreams.Publisher
import java.time.Instant

open class MetadataUpdater<M : MetadataModel> :
    java.util.function.Function<Pair<M, NFTMetadata>, Publisher<M>> {
    override fun apply(it: Pair<M, NFTMetadata>): Publisher<M> = mono {
        it.first.apply {
            name = it.second.name
            description = it.second.description
            image = it.second.image
            imageData = it.second.imageData
            coverImage = it.second.coverImage
            coverImageData = it.second.coverImageData

            metadataModified = Instant.now() // TODO
        }
    }
}

@Singleton
class CollectionMetadataUpdater : MetadataUpdater<CollectionModel>()

@Singleton
class ItemMetadataUpdater : MetadataUpdater<ItemModel>()
