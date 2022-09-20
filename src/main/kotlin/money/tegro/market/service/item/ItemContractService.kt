package money.tegro.market.service.item

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.service.ReferenceBlockService
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

@Service
class ItemContractService(
    private val liteClient: LiteClient,
    private val referenceBlockService: ReferenceBlockService,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<ItemContract>>().build()

    suspend fun get(address: MsgAddressInt): ItemContract? =
        cache.getOrPut(address) { item ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(item)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", item.toRaw()))
                Optional.empty()
            } else {
                try {
                    logger.debug("fetching item {}", kv("address", item.toRaw()))
                    ItemContract.of(item as AddrStd, liteClient, referenceBlockService.get())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn("could not get item information for {}", kv("address", item.toRaw()), e)
                    Optional.empty()
                }
            }
        }
            .orElse(null)

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.item.contract",
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
