package money.tegro.market.nightcrawler.writer

import money.tegro.market.db.CollectionRoyalty
import org.springframework.batch.integration.async.AsyncItemWriter

class CollectionRoyaltyAsyncWriter(collectionRoyaltyWriter: CollectionRoyaltyWriter) :
    AsyncItemWriter<CollectionRoyalty>() {
    init {
        setDelegate(collectionRoyaltyWriter)
    }
}
