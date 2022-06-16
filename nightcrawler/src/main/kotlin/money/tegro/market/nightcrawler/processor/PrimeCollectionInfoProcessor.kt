package money.tegro.market.nightcrawler.processor

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.findByAddress
import org.springframework.batch.item.ItemProcessor
import org.ton.block.MsgAddressIntStd

class PrimeCollectionInfoProcessor(private val collectionInfoRepository: CollectionInfoRepository) :
    ItemProcessor<String, CollectionInfo> {
    override fun process(item: String): CollectionInfo? {
        val address = MsgAddressIntStd(item)

        return collectionInfoRepository.findByAddress(address) ?: CollectionInfo(address)
    }
}
