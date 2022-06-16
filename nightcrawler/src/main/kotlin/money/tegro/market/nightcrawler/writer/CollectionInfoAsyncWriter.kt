package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionInfo
import org.springframework.batch.integration.async.AsyncItemWriter

class CollectionInfoAsyncWriter(collectionInfoWriter: CollectionInfoWriter) :
    AsyncItemWriter<CollectionInfo>() {
    init {
        setDelegate(collectionInfoWriter)
    }
}
