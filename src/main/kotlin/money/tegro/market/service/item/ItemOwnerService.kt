package money.tegro.market.service.item

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.service.SaleService
import mu.KLogging
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.ton.block.Block
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.util.*

@Service
class ItemOwnerService(
    private val itemContractService: ItemContractService,
    private val saleService: SaleService,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<MsgAddress>>().build()

    suspend fun get(address: MsgAddressInt): MsgAddress? =
        cache.getOrPut(address) { item ->
            val owner = itemContractService.get(item)?.owner
            ((owner as? MsgAddressInt)?.let { saleService.get(it)?.owner }
                ?: owner)
                .let { Optional.ofNullable(it) }
        }
            .orElse(null)

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.item.owner",
                ),
                exchange = Exchange(
                    name = "blocks",
                    type = ExchangeTypes.TOPIC,
                ),
                key = ["live"], // Only live blocks
            )
        ]
    )
    fun onLiveBlock(block: Block) {
        block.accountBlockAddresses()
            .forEach {
                cache.underlying().synchronous().invalidate(it as MsgAddressInt)
            }
    }

    companion object : KLogging()
}
