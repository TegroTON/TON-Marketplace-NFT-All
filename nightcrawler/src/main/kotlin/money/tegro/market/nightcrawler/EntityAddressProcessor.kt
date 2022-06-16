package money.tegro.market.nightcrawler

import money.tegro.market.db.AddressableEntity
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd

class EntityAddressProcessor : ItemProcessor<AddressableEntity, MsgAddressIntStd> {
    override fun process(item: AddressableEntity): MsgAddressIntStd? = item.addressStd()
}

@Configuration
class EntityAddressProcessorConfiguration {
    @Bean
    fun entityAddressProcessor() = EntityAddressProcessor()
}
