package money.tegro.market.nightcrawler

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.blockchain.nft.NFTRoyalty
import mu.KLogging
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

@Singleton
class UpdateDatabaseCollections(
    private val liteApi: LiteApi,

    private val collectionRepository: CollectionRepository,
) {
    @Scheduled(initialDelay = "5s")
    fun run() {
        logger.info { "Updating database collections" }

        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }

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
                    original.content?.let {
                        val nftRoyalty = NFTRoyalty.of(original.addressStd(), liteApi)

                        val new = original.copy().apply { // To check if anything was modified
                            numerator = nftRoyalty?.numerator
                            denominator = nftRoyalty?.denominator
                            destinationWorkchain = nftRoyalty?.destination?.workchainId
                            destinationAddress = nftRoyalty?.destination?.address?.toByteArray()
                        }

                        if (new == original) null else new
                    }
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

    companion object : KLogging()
}
