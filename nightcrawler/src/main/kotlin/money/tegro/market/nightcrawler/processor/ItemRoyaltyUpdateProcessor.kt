package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemRoyalty
import money.tegro.market.nft.NFTRoyalty
import money.tegro.market.ton.LiteApiFactory
import org.springframework.batch.integration.async.AsyncItemProcessor
import java.time.Instant

class ItemRoyaltyUpdateProcessor(
    private val liteApiFactory: LiteApiFactory
) : AsyncItemProcessor<ItemInfo, ItemRoyalty>() {
    init {
        setDelegate {
            runBlocking {
                if (it.collection == null) {
                    val royalty = NFTRoyalty.of(it.addressStd(), liteApiFactory.getObject())

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
