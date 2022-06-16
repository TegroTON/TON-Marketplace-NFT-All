package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTCollection
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd
import org.ton.lite.api.LiteApi


class NFTCollectionProcessor(private val liteApi: LiteApi) :
    ItemProcessor<MsgAddressIntStd, NFTCollection> {
    override fun process(it: MsgAddressIntStd): NFTCollection? {
        return runBlocking { NFTCollection.of(it, liteApi) }
    }
}


@Configuration
class NFTCollectionProcessorConfiguration(private val liteApi: LiteApi) {
    @Bean
    fun nftCollectionProcessor() = NFTCollectionProcessor(liteApi)
}
