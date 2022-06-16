package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemRoyalty
import money.tegro.market.nft.NFTRoyalty
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.lite.api.LiteApi
import java.time.Instant

class ItemRoyaltyUpdateProcessor(
    private val liteApi: LiteApi
) : AsyncItemProcessor<ItemInfo, ItemRoyalty>() {
    init {
        setDelegate {
            runBlocking {
                if (it.collection == null) {
                    val royalty = NFTRoyalty.of(it.addressStd(), liteApi)

                    (it.royalty ?: ItemRoyalty(it)).apply {
                        updated = Instant.now()
                        cneq(this::numerator, royalty?.numerator)
                        cneq(this::denominator, royalty?.denominator)
                        cneq(this::destinationWorkchain, royalty?.destination?.workchainId)
                        cneq(this::destinationAddress, royalty?.destination?.address?.toByteArray())
                    }
                } else {
                    null
                }
            }
        }
    }
}
