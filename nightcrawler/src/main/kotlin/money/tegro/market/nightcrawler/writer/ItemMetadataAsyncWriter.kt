package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.ItemMetadata
import org.springframework.batch.integration.async.AsyncItemWriter

class ItemMetadataAsyncWriter(itemMetadataWriter: ItemMetadataWriter) :
    AsyncItemWriter<ItemMetadata>() {
    init {
        setDelegate(itemMetadataWriter)
    }
}
