package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.ReferenceBlock
import org.reactivestreams.Publisher
import org.ton.lite.api.LiteApi

open class SaleProcess<RB : ReferenceBlock>(
    private val liteApi: LiteApi,
    private val referenceBlock: RB,
) : java.util.function.Function<AddressKey, Publisher<SaleModel>> {
    override fun apply(it: AddressKey): Publisher<SaleModel> = mono {
        NFTSale.of(it.to(), liteApi, referenceBlock())?.let { SaleModel(it) }
    }
}

@Prototype
class FixedSaleProcess(
    liteApi: LiteApi,
    referenceBlock: FixedReferenceBlock
) : SaleProcess<FixedReferenceBlock>(liteApi, referenceBlock)

@Prototype
class LatestSaleProcess(
    liteApi: LiteApi,
    referenceBlock: LatestReferenceBlock
) : SaleProcess<LatestReferenceBlock>(liteApi, referenceBlock)
