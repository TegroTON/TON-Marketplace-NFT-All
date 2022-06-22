package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import org.reactivestreams.Publisher
import reactor.kotlin.core.publisher.toFlux

@Singleton
class AttributeUpdater : java.util.function.Function<Pair<ItemModel, NFTMetadata>, Publisher<AttributeModel>> {
    override fun apply(it: Pair<ItemModel, NFTMetadata>): Publisher<AttributeModel> =
        it.second.attributes.orEmpty().toFlux()
            .map { attribute -> AttributeModel(it.first.address, attribute.trait, attribute.value) }
}
