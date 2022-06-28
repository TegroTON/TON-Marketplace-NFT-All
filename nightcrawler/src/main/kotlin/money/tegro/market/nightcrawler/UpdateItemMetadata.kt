package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.AttributeRepository
import money.tegro.market.core.repository.ItemRepository
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Prototype
class UpdateItemMetadata(
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<ItemModel, Mono<Void>> {
    override fun apply(it: ItemModel): Mono<Void> = mono {
        val content = it.collection?.to()?.let { collection ->
            it.index?.let { index ->
                it.content?.let { BagOfCells(it).roots.first() }?.let { individualContent ->
                    NFTDeployedCollectionItem.contentOf(
                        collection,
                        index,
                        individualContent,
                        liteApi,
                        referenceBlock.get()
                    )
                }
            }
        } ?: it.content?.let { BagOfCells(it).roots.first() }

        val metadata = content?.let { NFTMetadata.of(content) }
        itemRepository.update(
            it.address,
            metadata?.name,
            metadata?.description,
            metadata?.image,
            metadata?.imageData
        )

        metadata?.attributes.orEmpty()
            .toFlux()
            .concatMap { attribute ->
                mono {
                    attributeRepository.findByItemAndTraitForUpdate(it.address, attribute.trait)
                        .awaitSingleOrNull()?.let {
                            attributeRepository.update(it.id!!, attribute.value)
                        } ?: run {
                        attributeRepository.save(AttributeModel(it.address, attribute))
                    }
                }
            }.then().awaitSingleOrNull()
    }
}
