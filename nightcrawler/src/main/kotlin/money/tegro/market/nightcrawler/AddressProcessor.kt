package money.tegro.market.nightcrawler

import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd

class AddressProcessor : ItemProcessor<String, MsgAddressIntStd> {
    override fun process(item: String): MsgAddressIntStd? = MsgAddressIntStd(item)
}

@Configuration
class AddressProcessorConfiguration {
    @Bean
    fun addressProcessor() = AddressProcessor()
}
