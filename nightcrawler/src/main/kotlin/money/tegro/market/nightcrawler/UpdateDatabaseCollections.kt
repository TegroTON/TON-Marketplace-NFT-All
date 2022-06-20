package money.tegro.market.nightcrawler

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTDeployedCollection
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.model.CollectionData
import money.tegro.market.core.model.ItemData
import mu.KLogging
import org.ton.block.MsgAddressIntStd
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.Instant

@Singleton
class UpdateDatabaseCollections(
    private val resourceLoader: ClassPathResourceLoader,
    private val liteApi: LiteApi,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    @Scheduled(initialDelay = "0s")
    fun initializeCollections() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .filter { it.isNotBlank() }
                .map { MsgAddressIntStd(it) }
                .filter { !collectionRepository.existsByAddressStd(it) }
                .forEach {
                    collectionRepository.save(CollectionData(it)).subscribe()
                }
        } ?: run {
            logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    @Scheduled(initialDelay = "5s")
    fun updateEverything() {
        logger.info { "Updating database collections" }

        val updatedCollections = collectionRepository.findAll()
            .concatMap { original ->
                mono {
                    val nftCollection = NFTCollection.of(original.addressStd(), liteApi)

                    val new = original.copy().apply { // To check if anything was modified
                        nextItemIndex = nftCollection.nextItemIndex
                        ownerWorkchain = nftCollection.owner.workchainId
                        ownerAddress = nftCollection.owner.address.toByteArray()
                        content = BagOfCells(nftCollection.content).toByteArray()
                    }

                    if (new == original) original else new.apply { modified = Instant.now() }
                }
            }
            .replay()

        updatedCollections
            .subscribe {
                val id = it.id
                requireNotNull(id)
                collectionRepository.update(
                    id,
                    it.nextItemIndex,
                    it.ownerWorkchain,
                    it.ownerAddress,
                    it.content,
                    it.modified,
                )
            }

        updatedCollections
            .concatMap { original ->
                mono {
                    original.content?.let {
                        val nftMetadata = NFTMetadata.of(BagOfCells(it).roots.first())

                        val new = original.copy().apply { // To check if anything was modified
                            name = nftMetadata.name
                            description = nftMetadata.description
                            image = nftMetadata.image
                            imageData = nftMetadata.imageData
                            coverImage = nftMetadata.coverImage
                            coverImageData = nftMetadata.coverImageData
                        }

                        if (new == original) null else new
                    }
                }
            }.subscribe {
                val id = it.id
                requireNotNull(id)
                collectionRepository.update(
                    id,
                    it.name,
                    it.description,
                    it.image,
                    it.imageData,
                    it.coverImage,
                    it.coverImageData,
                )
            }

        updatedCollections
            .concatMap { original ->
                mono {
                    val nftRoyalty = NFTRoyalty.of(original.addressStd(), liteApi)

                    val new = original.copy().apply { // To check if anything was modified
                        numerator = nftRoyalty?.numerator
                        denominator = nftRoyalty?.denominator
                        destinationWorkchain = nftRoyalty?.destination?.workchainId
                        destinationAddress = nftRoyalty?.destination?.address?.toByteArray()
                    }

                    if (new == original) null else new
                }
            }.subscribe {
                val id = it.id
                requireNotNull(id)
                collectionRepository.update(
                    id,
                    it.numerator,
                    it.denominator,
                    it.destinationWorkchain,
                    it.destinationAddress,
                )
            }

        updatedCollections.connect()
    }

    @Scheduled(initialDelay = "10s")
    fun discoverMissingItems() {
        logger.info { "Discovering missing collection items" }

        collectionRepository.findAll()
            .concatMap { collection ->
                collection.nextItemIndex?.let { nextItemIndex ->
                    (0 until nextItemIndex).toFlux()
                        // Ignore items that are already added and indexed
                        .filter { !itemRepository.existsByIndexAndCollection(it, collection) }
                        .map { collection to it }
                }
            }
            .parallel()
            .runOn(Schedulers.boundedElastic())
            .concatMap {
                mono {
                    it.first to NFTDeployedCollection.itemAddressOf(it.first.addressStd(), it.second, liteApi)
                }
            }
            .filter { !itemRepository.existsByAddressStd(it.second) }
            .subscribe {
                itemRepository.save(ItemData(it.second).apply { collection = it.first }).subscribe()
            }
    }

    companion object : KLogging()
}
