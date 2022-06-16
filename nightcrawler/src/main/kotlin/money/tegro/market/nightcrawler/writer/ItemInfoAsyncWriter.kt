package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemInfo
import org.springframework.batch.integration.async.AsyncItemWriter

class ItemInfoAsyncWriter(itemInfoWriter: ItemInfoWriter) :
    AsyncItemWriter<ItemInfo>() {
    init {
        setDelegate(itemInfoWriter)
    }
}
