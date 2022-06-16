package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionMetadata
import org.springframework.batch.integration.async.AsyncItemWriter

class CollectionMetadataAsyncWriter(collectionMetadataWriter: CollectionMetadataWriter) :
    AsyncItemWriter<CollectionMetadata>() {
    init {
        setDelegate(collectionMetadataWriter)
    }
}
