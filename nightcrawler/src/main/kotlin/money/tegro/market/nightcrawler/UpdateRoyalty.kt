package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.repository.RoyaltyRepository
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono

@Prototype
class UpdateRoyalty(
    private val royaltyRepository: RoyaltyRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<AddressKey, Mono<Void>> {
    override fun apply(address: AddressKey): Mono<Void> = mono {
        (NFTRoyalty.of(address.to(), liteApi, referenceBlock.get())?.let { royalty ->
            royaltyRepository.upsert(address, royalty)
        }?.then() ?: Mono.empty()).awaitSingleOrNull()
    }
}
