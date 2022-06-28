package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono

@Prototype
class UpdateCollectionData(
    private val collectionRepository: CollectionRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<AddressKey, Mono<CollectionModel>> {
    override fun apply(address: AddressKey): Mono<CollectionModel> = mono {
        val collection = NFTCollection.of(address.to(), liteApi, referenceBlock.get())

        collectionRepository.update(
            address,
            collection.nextItemIndex,
            AddressKey.of(collection.owner),
            BagOfCells(collection.content).toByteArray(),
        )
        collectionRepository.findById(address).awaitSingleOrNull()
    }
}
