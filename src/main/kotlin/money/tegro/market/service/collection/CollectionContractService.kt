package money.tegro.market.service.collection

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.CollectionContract
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
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

@Service
class CollectionContractService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<CollectionContract>>().build()

    suspend fun get(
        address: MsgAddressInt,
        referenceBlock: suspend () -> TonNodeBlockIdExt? = { liteClient.getLastBlockId() }
    ): CollectionContract? =
        cache.getOrPut(address) { collection ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                try {
                    logger.debug("fetching collection {}", kv("address", collection.toRaw()))
                    CollectionContract.of(collection as AddrStd, liteClient, referenceBlock())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn("could not get collection information for {}", kv("address", collection.toRaw()), e)
                    Optional.empty()
                }
            } else {
                logger.warn("{} was not approved", kv("address", collection.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.collection.contract",
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
