package money.tegro.market.service

import kotlinx.coroutines.runBlocking
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.ton.api.tonnode.Shard
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.Block
import org.ton.lite.client.LiteClient

@Service
class ReferenceBlockService(
    private val liteClient: LiteClient,
) {
    private var referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getLastBlockId() }

    fun get() = referenceBlock

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.reference",
                    durable = "false",
                    autoDelete = "true",
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
        if (block.info.shard.workchain_id == -1) { // Masterchain
            if (referenceBlock.seqno < block.info.seq_no.toInt()) {
                referenceBlock = runBlocking { // TODO: fugly
                    liteClient.lookupBlock(
                        TonNodeBlockId(
                            workchain = block.info.shard.workchain_id,
                            shard = Shard.ID_ALL,
                            seqno = block.info.seq_no.toInt(),
                        )
                    )!!
                }
                logger.info("reference block {}", kv("seqno", referenceBlock.seqno))
            }
        }
    }

    companion object : KLogging()
}
