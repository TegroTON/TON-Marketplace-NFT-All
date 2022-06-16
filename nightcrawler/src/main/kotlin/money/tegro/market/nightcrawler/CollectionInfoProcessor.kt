package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.nft.NFTCollection
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.boc.BagOfCells
import java.time.Instant

class CollectionInfoProcessor(private val collectionInfoRepository: CollectionInfoRepository) :
    ItemProcessor<NFTCollection, CollectionInfo> {
    override fun process(it: NFTCollection): CollectionInfo? {
        val collection = collectionInfoRepository.findByAddress(it.address) ?: CollectionInfo(it.address)

        collection.updated = Instant.now()
        collection.cneq(collection::nextItemIndex, it.nextItemIndex)
        collection.cneq(collection::ownerWorkchain, it.owner.workchainId)
        collection.cneq(collection::ownerAddress, it.owner.address.toByteArray())
        collection.cneq(collection::content, BagOfCells(it.content).toByteArray())

        return collection
    }
}

@Configuration
class CollectionInfoProcessorConfiguration(private val collectionInfoRepository: CollectionInfoRepository) {
    @Bean
    fun collectionInfoProcessor() = CollectionInfoProcessor(collectionInfoRepository)
}
