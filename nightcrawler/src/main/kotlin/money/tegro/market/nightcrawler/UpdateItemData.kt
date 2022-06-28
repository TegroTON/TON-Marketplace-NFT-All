package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono

@Prototype
class UpdateItemData(
    private val itemRepository: ItemRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<AddressKey, Mono<ItemModel>> {
    override fun apply(address: AddressKey): Mono<ItemModel> = mono {
        val item = NFTItem.of(address.to(), liteApi, referenceBlock.get())

        itemRepository.update(
            address,
            item != null,
            item?.index,
            (item as? NFTDeployedCollectionItem)?.collection?.let { AddressKey.of(it) },
            item?.owner?.let { AddressKey.of(it) },
            item?.individualContent?.let { BagOfCells(it).toByteArray() }
        )
        itemRepository.findById(address).awaitSingleOrNull()
    }
}
