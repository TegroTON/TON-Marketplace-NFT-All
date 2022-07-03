package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.ItemModel
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

@Prototype
class ItemProcess(
    private val liteApi: LiteApi,
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { it: ItemModel ->
            mono {
                val item = NFTItem.of(it.address.to(), liteApi, referenceBlock)

                it.copy(item)?.copy(item.metadata(liteApi, referenceBlock))
                    ?: it.apply {
                        logger.warn { "Could not update item ${it.address.toSafeBounceable()}, something went wrong" }
                    }
            }
        }

    companion object : KLogging()
}
