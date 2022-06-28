package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import io.micronaut.data.model.Sort
import kotlinx.coroutines.reactor.awaitSingleOrNull
import money.tegro.market.core.repository.CollectionRepository
import mu.KLogging
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Prototype
class CatchUpOnCollectionsStep(
    private val referenceBlock: ReferenceBlock,
    private val collectionRepository: CollectionRepository,

    private val updateCollectionData: UpdateCollectionData,
    private val updateCollectionMetadata: UpdateCollectionMetadata,
    private val updateRoyalty: UpdateRoyalty,
    private val discoverMissingItems: DiscoverMissingItems,
) {
    suspend fun run() {
        val seqno = referenceBlock.get().invoke().seqno
        logger.info { "Updating collections up to block no. $seqno" }

        val collections = collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .publishOn(Schedulers.boundedElastic())
            .replay()

        val data = collections
            .map { it.address }
            .concatMap(updateCollectionData)
            .replay()

        val metadata = data
            .concatMap(updateCollectionMetadata)

        val royalty = collections
            .map { it.address }
            .concatMap(updateRoyalty)

        data.connect()
        collections.connect()

        Mono.`when`(data, metadata, royalty)
            .awaitSingleOrNull()

        logger.info { "Discovering missing items" }
        collectionRepository.findAll(Sort.of(Sort.Order.asc("updated")))
            .map { it.address to (it.nextItemIndex ?: 0) }
            .concatMap(discoverMissingItems)
            .then()
            .awaitSingleOrNull()

        logger.info { "Collections up-to-date at block height $seqno" }
    }

    companion object : KLogging()
}
