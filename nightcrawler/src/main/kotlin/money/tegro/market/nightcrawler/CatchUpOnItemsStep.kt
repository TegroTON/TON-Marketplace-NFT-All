package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import io.micronaut.data.model.Sort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import money.tegro.market.core.repository.ItemRepository
import mu.KLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Prototype
class CatchUpOnItemsStep(
    private val referenceBlock: ReferenceBlock,
    private val itemRepository: ItemRepository,
    private val updateItemData: UpdateItemData,
    private val updateSaleData: UpdateSaleData,
    private val updateItemMetadata: UpdateItemMetadata,
    private val updateRoyalty: UpdateRoyalty,
) {
    suspend fun run() {
        val seqno = referenceBlock.get().invoke().seqno
        logger.info { "Updating items up to block no. $seqno" }

        val items = itemRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .publishOn(Schedulers.boundedElastic())
            .replay()

        val data = items
            .map { it.address }
            .concatMap(updateItemData)
            .replay()

        val sale = data
            .concatMap {
                it.owner?.let { updateSaleData.apply(it) } ?: Mono.empty()
            }

        val metadata = data
            .concatMap(updateItemMetadata)

        val royalty = items
            .filter { it.collection != null } // only stand-alone items
            .map { it.address }
            .concatMap(updateRoyalty)

        items.connect()
        data.connect()

        Mono.`when`(data, sale, metadata, royalty).awaitSingleOrNull() // Wait for all of them to complete

        logger.info { "Items up-to-date at block height $seqno" }
    }

    companion object : KLogging()
}
