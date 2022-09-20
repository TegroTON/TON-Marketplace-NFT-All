package money.tegro.market.service.item

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.ItemMetadata
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
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class ItemMetadataService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
    private val itemContractService: ItemContractService,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, ItemMetadata?>().build()

    suspend fun get(address: MsgAddressInt): ItemMetadata? =
        cache.getOrPut(address) { item ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(item)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", item.toRaw()))
                null
            } else {
                itemContractService.get(item)?.let { contract ->
                    ItemMetadata.of(
                        (contract.collection as? AddrStd) // Collection items
                            ?.let {
                                CollectionContract.itemContent(
                                    it,
                                    contract.index,
                                    contract.individual_content,
                                    liteClient
                                )
                            }
                            ?: contract.individual_content) // Standalone items
                }
            }
        }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.item.metadata",
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
