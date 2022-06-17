package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemSale
import money.tegro.market.nft.NFTSale
import money.tegro.market.ton.LiteApiFactory
import org.springframework.batch.integration.async.AsyncItemProcessor
import java.time.Instant

class ItemSaleUpdateProcessor(
    private val liteApiFactory: LiteApiFactory
) : AsyncItemProcessor<ItemInfo, ItemSale>() {
    init {
        setDelegate { item ->
            runBlocking {
                val sale = NFTSale.of(item.addressStd(), liteApiFactory.getObject())

                (item.sale ?: ItemSale(item)).apply {
                    updated = Instant.now()

                    cneq(this::workchain, sale?.address?.workchainId)
                    cneq(this::address, sale?.address?.address?.toByteArray())
                    cneq(this::marketplaceWorkchain, sale?.marketplace?.workchainId)
                    cneq(this::marketplaceAddress, sale?.marketplace?.address?.toByteArray())
                    cneq(this::marketplaceFee, sale?.marketplaceFee)
                    cneq(this::ownerWorkchain, sale?.owner?.workchainId)
                    cneq(this::ownerAddress, sale?.owner?.address?.toByteArray())
                    cneq(this::price, sale?.price)
                    cneq(this::royalty, sale?.royalty)
                    cneq(this::royaltyDestinationWorkchain, sale?.royaltyDestination?.workchainId)
                    cneq(this::royaltyDestinationAddress, sale?.royaltyDestination?.address?.toByteArray())
                }
            }
        }
    }
}
