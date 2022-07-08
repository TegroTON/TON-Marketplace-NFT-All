package money.tegro.market.nightcrawler.worker

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.nft.NFTItemMetadataAttribute
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.AttributeRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.nightcrawler.NightcrawlerConfiguration
import money.tegro.market.nightcrawler.WorkSinks.emitNextAccount
import money.tegro.market.nightcrawler.WorkSinks.emitNextCollection
import money.tegro.market.nightcrawler.WorkSinks.emitNextRoyalty
import money.tegro.market.nightcrawler.WorkSinks.emitNextSale
import money.tegro.market.nightcrawler.WorkSinks.items
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import java.time.Duration
import java.time.Instant

@Singleton
class ItemWorker(
    private var liteApi: LiteApi,
    private var configuration: NightcrawlerConfiguration,
    private var itemRepository: ItemRepository,
    private var attributeRepository: AttributeRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "setting up item worker" }

        items
            .asFlux()
            .concatMap(::processItem)
            .subscribe()
    }

    private fun processItem(address: AddrStd) = mono {
        itemRepository.findById(address).awaitSingleOrNull()?.let { dbItem ->
            if (Duration.between(dbItem.updated, Instant.now()) > configuration.itemUpdatePeriod) {
                logger.debug(
                    "updating existing item {} blockchain data",
                    StructuredArguments.value("address", dbItem.address)
                )

                val item = NFTItem.of(dbItem.address, liteApi)

                var new = dbItem.copy(
                    initialized = item.initialized,
                    index = item.index,
                    collection = item.collection,
                    owner = item.owner,
                    updated = Instant.now(),
                )

                if (Duration.between(dbItem.metadataUpdated, Instant.now()) > configuration.itemMetadataUpdatePeriod) {
                    logger.debug(
                        "updating existing item {} metadata",
                        StructuredArguments.value("address", dbItem.address)
                    )
                    val metadata = item.metadata(liteApi)

                    new = new.copy(
                        name = metadata.name.orEmpty(),
                        description = metadata.description.orEmpty(),
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        metadataUpdated = Instant.now()
                    )

                    processItemAttributes(dbItem.address, metadata.attributes.orEmpty())
                } else {
                    logger.debug(
                        "item {} metadata is up-to-date, last updated {}",
                        StructuredArguments.value("address", dbItem.address),
                        StructuredArguments.value("updated", dbItem.metadataUpdated)
                    )
                }

                // Trigger other jobs
                (dbItem.owner as? AddrStd)?.let { emitNextAccount(it); emitNextSale(it) }
                if (dbItem.owner != new.owner) // In case owner was changed, update both
                    (new.owner as? AddrStd)?.let { emitNextAccount(it); emitNextSale(it) }

                (new.collection as? AddrStd)?.let {
                    emitNextCollection(it)
                }
                if (new.collection is AddrNone)
                    emitNextRoyalty(new.address)

                itemRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "item {} blockchain data is up-to-date, last updated {}",
                    StructuredArguments.value("address", dbItem.address),
                    StructuredArguments.value("updated", dbItem.updated)
                )
                dbItem
            }
        } ?: run {
            logger.debug("saving new item {}", StructuredArguments.value("address", address))

            val item = NFTItem.of(address, liteApi)
            val metadata = item.metadata(liteApi)

            val new = ItemModel(
                address = item.address as AddrStd,
                initialized = item.initialized,
                index = item.index,
                collection = item.collection,
                owner = item.owner,
                name = metadata.name.orEmpty(),
                description = metadata.description.orEmpty(),
                image = metadata.image,
                imageData = metadata.imageData ?: byteArrayOf(),
            )

            processItemAttributes(address, metadata.attributes.orEmpty())

            // Trigger other jobs
            (new.owner as? AddrStd)?.let { emitNextAccount(it); emitNextSale(it) }

            (new.collection as? AddrStd)?.let { emitNextCollection(it) }
            if (new.collection is AddrNone)
                emitNextRoyalty(new.address)

            itemRepository.save(new).awaitSingleOrNull()
        }
    }

    private fun processItemAttributes(address: AddrStd, attributes: Iterable<NFTItemMetadataAttribute>) {
        attributes.forEach { attribute ->
            mono {
                attributeRepository.findByItemAndTrait(address, attribute.trait)
                    .awaitSingleOrNull()?.let {
                        attributeRepository.update(it.copy(value = attribute.value)).subscribe()
                    } ?: run {
                    attributeRepository.save(
                        AttributeModel(
                            address,
                            attribute.trait,
                            attribute.value
                        )
                    ).subscribe()
                }
            }.subscribe()
        }
    }

    companion object : KLogging()
}
