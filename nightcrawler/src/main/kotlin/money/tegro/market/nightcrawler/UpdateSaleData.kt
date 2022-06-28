package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.repository.SaleRepository
import org.ton.lite.api.LiteApi
import reactor.core.publisher.Mono

@Prototype
class UpdateSaleData(
    private val saleRepository: SaleRepository,
    private val liteApi: LiteApi,
    private val referenceBlock: ReferenceBlock
) : java.util.function.Function<AddressKey, Mono<Void>> {
    override fun apply(address: AddressKey): Mono<Void> = mono {
        (NFTSale.of(address.to(), liteApi, referenceBlock.get())?.let { sale ->
            saleRepository.upsert(address, sale)
        }?.then() ?: Mono.empty()).awaitSingleOrNull()
    }
}
