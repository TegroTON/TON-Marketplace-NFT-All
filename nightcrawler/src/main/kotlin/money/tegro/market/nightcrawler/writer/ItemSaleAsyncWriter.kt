package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemSale
import org.springframework.batch.integration.async.AsyncItemWriter

class ItemSaleAsyncWriter(itemSaleWriter: ItemSaleWriter) : AsyncItemWriter<ItemSale>() {
    init {
        setDelegate(itemSaleWriter)
    }
}
