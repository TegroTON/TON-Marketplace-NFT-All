package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.nightcrawler.FixedReferenceBlock
import money.tegro.market.nightcrawler.LatestReferenceBlock
import money.tegro.market.nightcrawler.ReferenceBlock
import org.reactivestreams.Publisher
import org.ton.lite.api.LiteApi

open class RoyaltyProcess<RB : ReferenceBlock>(
    private val liteApi: LiteApi,
    private val referenceBlock: RB,
) : java.util.function.Function<AddressKey, Publisher<RoyaltyModel>> {
    override fun apply(it: AddressKey): Publisher<RoyaltyModel> = mono {
        NFTRoyalty.of(it.to(), liteApi, referenceBlock())?.let { royalty ->
            RoyaltyModel(it, royalty)
        }
    }
}

@Prototype
class FixedRoyaltyProcess(
    liteApi: LiteApi,
    referenceBlock: FixedReferenceBlock
) : RoyaltyProcess<FixedReferenceBlock>(liteApi, referenceBlock)

@Prototype
class LatestRoyaltyProcess(
    liteApi: LiteApi,
    referenceBlock: LatestReferenceBlock
) : RoyaltyProcess<LatestReferenceBlock>(liteApi, referenceBlock)
