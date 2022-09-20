package money.tegro.market.service.collection

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.metadata.CollectionMetadata
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
import org.ton.block.Block
import org.ton.block.MsgAddressInt
import java.util.*

@Service
class CollectionMetadataService(
    private val collectionContractService: CollectionContractService,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<CollectionMetadata>>().build()

    suspend fun get(address: MsgAddressInt): CollectionMetadata? =
        cache.getOrPut(address) { collection ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                collectionContractService.get(collection)
                    ?.let {
                        CollectionMetadata.of(it.content)
                    }
                    .let { Optional.ofNullable(it) }
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
                    name = "blocks.market.collection.metadata",
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
