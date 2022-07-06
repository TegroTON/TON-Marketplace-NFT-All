package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTException
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions

@Prototype
class SaleProcess(
    private val liteApi: LiteApi,
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { it: AddressKey ->
            mono {
                try {
                    SaleModel.of(NFTSale.of(it.to(), liteApi, referenceBlock))
                } catch (e: NFTException) {
                    logger.warn(e) { "failed to get sale, most likely address isn't a sale contract" }
                    Exceptions.propagate(e)
                    null
                }
            }
        }

    companion object : KLogging()
}
