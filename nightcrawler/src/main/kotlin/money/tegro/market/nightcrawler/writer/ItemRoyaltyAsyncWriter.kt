package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemRoyalty
import org.springframework.batch.integration.async.AsyncItemWriter

class ItemRoyaltyAsyncWriter(itemRoyaltyWriter: ItemRoyaltyWriter) :
    AsyncItemWriter<ItemRoyalty>() {
    init {
        setDelegate(itemRoyaltyWriter)
    }
}
