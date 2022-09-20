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
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

@Service
class CollectionItemAddressService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<Pair<MsgAddressInt, ULong>, Optional<MsgAddress>>().build()

    suspend fun get(
        address: MsgAddressInt,
        index: ULong,
        referenceBlock: suspend () -> TonNodeBlockIdExt? = { liteClient.getLastBlockId() }
    ): MsgAddress? =
        cache.getOrPut(address to index) { (collection, index) ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                try {
                    CollectionContract.itemAddressOf(collection as AddrStd, index, liteClient, referenceBlock())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn(
                        "could not get item {} address of {}",
                        kv("index", index.toString()),
                        kv("collection", collection.toRaw())
                    )
                    Optional.empty()
                }
            } else {
                logger.warn("{} was not approved", kv("address", address.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.collection.item_address",
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
                val s = cache.underlying().synchronous()
                s.asMap().keys
                    .filter { it.first == it }
                    .forEach {
                        s.invalidate(it)
                    }
            }
    }

    companion object : KLogging()
}
