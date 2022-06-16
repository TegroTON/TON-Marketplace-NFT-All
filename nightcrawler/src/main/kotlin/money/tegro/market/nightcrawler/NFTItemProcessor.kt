package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTItem
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd
import org.ton.lite.api.LiteApi

class NFTItemProcessor(private val liteApi: LiteApi) :
    ItemProcessor<MsgAddressIntStd, Pair<MsgAddressIntStd, NFTItem?>> {
    override fun process(it: MsgAddressIntStd): Pair<MsgAddressIntStd, NFTItem?>? {
        return runBlocking { it to NFTItem.of(it, liteApi) }
    }
}

@Configuration
class NFTItemProcessorConfiguration(private val liteApi: LiteApi) {
    @Bean
    fun nftItemProcessor() = NFTItemProcessor(liteApi)
}
