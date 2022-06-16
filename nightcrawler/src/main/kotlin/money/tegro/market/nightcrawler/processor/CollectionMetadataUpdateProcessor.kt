package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionMetadata
import money.tegro.market.nft.NFTMetadata
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.boc.BagOfCells
import java.time.Instant

class CollectionMetadataUpdateProcessor : AsyncItemProcessor<CollectionInfo, CollectionMetadata>() {
    init {
        setDelegate {
            runBlocking {
                val metadata = it.content?.let { BagOfCells(it).roots.first() }?.let { NFTMetadata.of(it) }

                (it.metadata ?: CollectionMetadata(it)).apply {
                    updated = Instant.now()
                    cneq(this::name, metadata?.name)
                    cneq(this::description, metadata?.description)
                    cneq(this::image, metadata?.image)
                    cneq(this::imageData, metadata?.imageData)
                    cneq(this::coverImage, metadata?.coverImage)
                    cneq(this::coverImageData, metadata?.coverImageData)
                }
            }
        }
    }
}
