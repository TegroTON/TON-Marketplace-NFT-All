package money.tegro.market.service

import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import org.ton.block.AddrStd
import org.ton.block.Block

@Service
class WatchService(
    private val collectionService: CollectionService,
    private val itemService: ItemService,
    private val royaltyService: RoyaltyService,
    private val saleService: SaleService,
) {
    @RabbitListener(queues = ["market.blocks"])
    fun blocks(block: Block) {
        logger.info("got block {} {}", kv("workchain", block.info.shard.workchain_id), kv("seqno", block.info.seq_no))

        // TODO: Use events or some queue
        block.extra.account_blocks.toMap()
            .keys
            .map { AddrStd(block.info.shard.workchain_id, it.account_addr) }
            .forEach {
                collectionService.onChange(it)
                itemService.onChange(it)
                royaltyService.onChange(it)
                saleService.onChange(it)
            }
    }

    companion object : KLogging()
}
