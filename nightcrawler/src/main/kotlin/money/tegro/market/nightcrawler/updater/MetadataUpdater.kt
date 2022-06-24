package money.tegro.market.nightcrawler.updater

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import org.reactivestreams.Publisher
import java.time.Instant

abstract class MetadataUpdater<M : MetadataModel> :
    java.util.function.Function<Pair<M, NFTMetadata>, Publisher<M>> {
    abstract fun copy(it: M): M // Data classes define .copy() but we cannot use it since this class operates on MetadataModel interface

    override fun apply(it: Pair<M, NFTMetadata>): Publisher<M> = mono {
        val new = copy(it.first).apply {
            name = it.second.name
            description = it.second.description
            image = it.second.image
            imageData = it.second.imageData
            coverImage = it.second.coverImage
            coverImageData = it.second.coverImageData
        }

        if (new == it.first) it.first else new.apply { metadataModified = Instant.now() }
    }
}

@Singleton
class CollectionMetadataUpdater : MetadataUpdater<CollectionModel>() {
    override fun copy(it: CollectionModel): CollectionModel = it.copy()
}

@Singleton
class ItemMetadataUpdater : MetadataUpdater<ItemModel>() {
    override fun copy(it: ItemModel): ItemModel = it.copy()
}
