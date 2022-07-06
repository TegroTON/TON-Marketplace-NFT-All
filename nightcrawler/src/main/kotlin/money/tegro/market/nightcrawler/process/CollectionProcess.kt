package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.CollectionModel
import mu.KLogging
import mu.withLoggingContext
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

@Prototype
class CollectionProcess(
    private val liteApi: LiteApi,
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { it: CollectionModel ->
            mono {
                val collection = NFTCollection.of(it.address.to(), liteApi, referenceBlock)

                it.copy(collection)?.copy(collection.metadata())
                    ?: it.apply {
                        withLoggingContext("address" to it.address.toSafeBounceable()) {
                            logger.warn { "could not update collection ${it.address.toSafeBounceable()}, something went wrong" }
                        }
                    }
            }
        }

    companion object : KLogging()
}
