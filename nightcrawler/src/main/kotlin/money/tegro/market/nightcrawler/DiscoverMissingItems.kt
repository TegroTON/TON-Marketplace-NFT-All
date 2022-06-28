package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTDeployedCollection
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux

@Prototype
class DiscoverMissingItems(
    private val itemRepository: ItemRepository,
    val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<Pair<AddressKey, Long>, Mono<Void>> {
    override fun apply(it: Pair<AddressKey, Long>): Mono<Void> = mono {
        val (address, nextItemIndex) = it

        (0 until nextItemIndex)
            .toFlux()
            .filterWhen {
                // Ignore items that are already added and indexed
                itemRepository.existsByIndexAndCollection(it, address).map { !it }
            }
            .map { address.to() to it }
            .publishOn(Schedulers.boundedElastic())
            .concatMap {
                mono { NFTDeployedCollection.itemAddressOf(it.first, it.second, liteApi, referenceBlock.get()) }
            }
            .filterWhen { itemRepository.existsByAddress(it).map { !it } }
            .concatMap { itemRepository.save(ItemModel(it)) }
            .then()
            .awaitSingleOrNull()
    }
}
