package money.tegro.market.nightcrawler.processor

import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.db.findByAddress
import org.springframework.batch.item.ItemProcessor
import org.ton.block.MsgAddressIntStd

class PrimeItemInfoProcessor(private val itemInfoRepository: ItemInfoRepository) :
    ItemProcessor<String, ItemInfo> {
    override fun process(item: String): ItemInfo? {
        val address = MsgAddressIntStd(item)

        return itemInfoRepository.findByAddress(address) ?: ItemInfo(address)
    }
}
