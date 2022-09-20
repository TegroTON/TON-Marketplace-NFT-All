package money.tegro.market.service

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.repository.ApprovalRepository
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

@Service
class SaleService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, SaleContract?>().build()

    suspend fun get(address: MsgAddressInt): SaleContract? =
        cache.getOrPut(address) { sale ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(sale)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", sale.toRaw()))
                null
            } else {
                try {
                    logger.debug("fetching sale information {}", kv("address", sale.toRaw()))
                    SaleContract.of(sale as AddrStd, liteClient)
                } catch (e: TvmException) {
                    logger.warn("could not get sale information for {}", kv("address", sale.toRaw()), e)
                    null
                }
            }
        }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.sale",
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
