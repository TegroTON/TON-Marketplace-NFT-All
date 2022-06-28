package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import org.ton.boc.BagOfCells
import reactor.core.publisher.Mono

@Prototype
class UpdateCollectionMetadata(
    private val collectionRepository: CollectionRepository,
) : java.util.function.Function<CollectionModel, Mono<Void>> {
    override fun apply(it: CollectionModel): Mono<Void> = mono {
        val metadata = it.content?.let { BagOfCells(it).roots.first() }?.let { NFTMetadata.of(it) }

        collectionRepository.update(
            it.address,
            metadata?.name,
            metadata?.description,
            metadata?.image,
            metadata?.imageData,
            metadata?.coverImage,
            metadata?.coverImageData
        )
        Mono.empty<Void>().awaitSingleOrNull()
    }
}
