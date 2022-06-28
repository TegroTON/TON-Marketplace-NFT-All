package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.AttributeRepository
import org.reactivestreams.Publisher
import java.time.Instant

@Prototype
class ItemMetadataProcess(private val attributeRepository: AttributeRepository) :
    java.util.function.Function<ItemModel, Publisher<ItemModel>> {
    override fun apply(it: ItemModel): Publisher<ItemModel> = mono {
        it.apply {
            content()?.let { NFTMetadata.of(it) }?.let { metadata ->
                name = metadata.name
                description = metadata.description
                image = metadata.image
                imageData = metadata.imageData

                metadata.attributes
                    .orEmpty()
                    .forEach {
                        attributeRepository.upsert(AttributeModel(address, it))
                            .subscribe()
                    }

                metadataUpdated = Instant.now()
            }
        }
    }
}
