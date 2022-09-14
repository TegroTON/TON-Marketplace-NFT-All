package money.tegro.market.configuration

import mu.KLogging
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Configuration
import org.ton.block.Block
import org.ton.boc.BagOfCells
import org.ton.tlb.loadTlb

@Configuration
class RabbitConfiguration {
    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(name = "blocks_market"),
            exchange = Exchange(name = "blocks", ignoreDeclarationExceptions = Exchange.TRUE),
            declare = "true",
        )]
    )
    fun blocks(raw: ByteArray) {
        val block = BagOfCells(raw).roots.first().parse { loadTlb(Block) }
        logger.info("workchain ${block.info.shard.workchain_id} seqno ${block.info.seq_no}")
    }

    companion object : KLogging()
}
