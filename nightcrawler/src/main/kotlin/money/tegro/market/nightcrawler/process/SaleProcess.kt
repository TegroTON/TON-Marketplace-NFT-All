package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

@Prototype
class SaleProcess(
    private val liteApi: LiteApi,
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { it: AddressKey ->
            mono { SaleModel.of(NFTSale.of(it.to(), liteApi, referenceBlock)) }
        }

    companion object : KLogging()
}
