package money.tegro.market.nightcrawler.worker

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.collections
import money.tegro.market.nightcrawler.WorkSinks.emitNextAccount
import money.tegro.market.nightcrawler.WorkSinks.emitNextItem
import money.tegro.market.nightcrawler.WorkSinks.emitNextRoyalty
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.bool.not
import java.time.Duration
import java.time.Instant

@Singleton
class CollectionWorker(
    private var liteApi: LiteApi,
    private var configuration: NightcrawlerConfiguration,
    private var collectionRepository: CollectionRepository,
    private var itemRepository: ItemRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "setting up collection worker" }

        collections
            .asFlux()
            .concatMap(::processCollection)
            .concatMap(::processMissingItems)
            .subscribe()
    }

    private fun processCollection(address: AddrStd) = mono {
        collectionRepository.findById(address).awaitSingleOrNull()?.let { dbCollection ->
            if (Duration.between(dbCollection.updated, Instant.now()) > configuration.collectionUpdatePeriod) {
                logger.debug(
                    "updating existing collection {} blockchain data",
                    StructuredArguments.value("address", dbCollection.address)
                )

                val collection = NFTCollection.of(dbCollection.address, liteApi)

                var new = dbCollection.copy(
                    nextItemIndex = collection.nextItemIndex,
                    owner = collection.owner,
                    updated = Instant.now(),
                )

                if (Duration.between(
                        dbCollection.metadataUpdated,
                        Instant.now()
                    ) > configuration.collectionMetadataUpdatePeriod
                ) {
                    logger.debug(
                        "updating existing collection {} metadata",
                        StructuredArguments.value("address", dbCollection.address)
                    )
                    val metadata = collection.metadata()

                    new = new.copy(
                        name = metadata.name,
                        description = metadata.description,
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        coverImage = metadata.coverImage,
                        coverImageData = metadata.coverImageData ?: byteArrayOf(),
                        metadataUpdated = Instant.now(),
                    )
                } else {
                    logger.debug(
                        "collection {} metadata is up-to-date, last updated {}",
                        StructuredArguments.value("address", dbCollection.address),
                        StructuredArguments.value("updated", dbCollection.metadataUpdated)
                    )
                }

                // Trigger other jobs
                (dbCollection.owner as? AddrStd)?.let { emitNextAccount(it) }
                if (dbCollection.owner != new.owner) // In case owner was changed, update both
                    (new.owner as? AddrStd)?.let {
                        emitNextAccount(it)
                    }

                emitNextRoyalty(new.address)

                collectionRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "collection {} blockchain data is up-to-date, last updated {}",
                    StructuredArguments.value("address", dbCollection.address),
                    StructuredArguments.value("updated", dbCollection.updated)
                )
                dbCollection
            }
        } ?: run {
            logger.debug("saving new collection {}", StructuredArguments.value("address", address))

            val collection = NFTCollection.of(address, liteApi)
            val metadata = collection.metadata()

            val new = CollectionModel(
                address = collection.address as AddrStd,
                nextItemIndex = collection.nextItemIndex,
                owner = collection.owner,
                name = metadata.name,
                description = metadata.description,
                image = metadata.image,
                imageData = metadata.imageData ?: byteArrayOf(),
                coverImage = metadata.coverImage,
                coverImageData = metadata.coverImageData ?: byteArrayOf(),
            )

            // Trigger other jobs
            (new.owner as? AddrStd)?.let { emitNextAccount(it) }

            emitNextRoyalty(new.address)

            collectionRepository.save(new).awaitSingleOrNull()
        }
    }

    private fun processMissingItems(collection: CollectionModel) = mono {
        (0 until collection.nextItemIndex)
            .toFlux()
            .filterWhen { index ->
                // Ignore items that are already added and indexed
                itemRepository.existsByIndexAndCollection(index, collection.address).not()
            }
            .concatMap { index ->
                mono {
                    logger.debug(
                        "fetching missing collection {} item no. {}",
                        StructuredArguments.value("collection", collection.address),
                        StructuredArguments.value("index", index)
                    )
                    NFTCollection.itemAddressOf(collection.address, index, liteApi) as? AddrStd
                    // If not addrstd, this is null and we just skip it
                }
            }
            .filterWhen { itemRepository.existsById(it).not() } // Isn't in the repository?
            .doOnNext {
                emitNextItem(it)
            }
            .then()
            .awaitSingleOrNull()
    }

    companion object : KLogging()
}
