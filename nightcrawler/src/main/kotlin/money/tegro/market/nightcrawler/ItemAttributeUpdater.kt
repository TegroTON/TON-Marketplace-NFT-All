package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.ItemAttributeModel
import money.tegro.market.core.model.ItemModel
import org.reactivestreams.Publisher
import reactor.kotlin.core.publisher.toFlux

@Singleton
class ItemAttributeUpdater : java.util.function.Function<Pair<ItemModel, NFTMetadata>, Publisher<ItemAttributeModel>> {
    override fun apply(it: Pair<ItemModel, NFTMetadata>): Publisher<ItemAttributeModel> =
        it.second.attributes.orEmpty().toFlux()
            .map { attribute -> ItemAttributeModel(it.first.address, attribute.trait, attribute.value) }
}
