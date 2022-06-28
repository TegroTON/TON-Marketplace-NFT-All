package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import org.reactivestreams.Publisher
import java.time.Instant

@Prototype
class CollectionMetadataProcess : java.util.function.Function<CollectionModel, Publisher<CollectionModel>> {
    override fun apply(it: CollectionModel): Publisher<CollectionModel> = mono {
        it.apply {
            content()?.let { NFTMetadata.of(it) }?.let { metadata ->
                name = metadata.name
                description = metadata.description
                image = metadata.image
                imageData = metadata.imageData
                coverImage = metadata.coverImage
                coverImageData = metadata.coverImageData
                metadataUpdated = Instant.now()
            }
        }
    }
}
