package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionRoyalty
import money.tegro.market.nft.NFTRoyalty
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.lite.api.LiteApi
import java.time.Instant

class CollectionRoyaltyUpdateProcessor(
    private val liteApi: LiteApi
) : AsyncItemProcessor<CollectionInfo, CollectionRoyalty>() {
    init {
        setDelegate {
            runBlocking {
                val royalty = NFTRoyalty.of(it.addressStd(), liteApi)

                (it.royalty ?: CollectionRoyalty(it)).apply {
                    updated = Instant.now()
                    cneq(this::numerator, royalty?.numerator)
                    cneq(this::denominator, royalty?.denominator)
                    cneq(this::destinationWorkchain, royalty?.destination?.workchainId)
                    cneq(this::destinationAddress, royalty?.destination?.address?.toByteArray())
                }
            }
        }
    }
}
