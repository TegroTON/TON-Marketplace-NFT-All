package money.tegro.market.nightcrawler

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.blockchain.nft.NFTRoyalty
import mu.KLogging
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

@Singleton
class UpdateDatabaseItems(
    private val liteApi: LiteApi,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    @Scheduled(initialDelay = "30s")
    fun updateEverything() {
        logger.info { "Updating database items" }

        val updatedItems = itemRepository.findAll()
            .concatMap { original ->
                mono {
                    val nftItem = NFTItem.of(original.addressStd(), liteApi)

                    val new = original.copy().apply { // To check if anything was modified
                        index = nftItem?.index
                        collection = (nftItem as? NFTDeployedCollectionItem)?.collection?.let {
                            collectionRepository.findByAddressStd(it).block()
                        }
                        ownerWorkchain = nftItem?.owner?.workchainId
                        ownerAddress = nftItem?.owner?.address?.toByteArray()
                        content = nftItem?.individualContent?.let { BagOfCells(it).toByteArray() }
                    }

                    if (new == original) original else new.apply { modified = Instant.now() }
                }
            }
            .replay()

        updatedItems
            .subscribe {
                val id = it.id
                requireNotNull(id)
                itemRepository.update(
                    id,
                    it.index,
                    it.collection,
                    it.ownerWorkchain,
                    it.ownerAddress,
                    it.content,
                    it.modified,
                )
            }

        updatedItems
            .concatMap { original ->
                mono {
                    original.content?.let {
                        val nftMetadata =
                            original.content?.let { BagOfCells(it).roots.first() }?.let { individualContent ->
                                original.collection?.addressStd()?.let { collection ->
                                    original.index?.let { index ->
                                        NFTMetadata.of(
                                            NFTDeployedCollectionItem.contentOf(
                                                collection,
                                                index,
                                                individualContent,
                                                liteApi
                                            )
                                        )
                                    }
                                } ?: NFTMetadata.of(individualContent)
                            }

                        val new = original.copy().apply { // To check if anything was modified
                            name = nftMetadata?.name
                            description = nftMetadata?.description
                            image = nftMetadata?.image
                            imageData = nftMetadata?.imageData
                        }

                        if (new == original) null else new
                    }
                }
            }.subscribe {
                val id = it.id
                requireNotNull(id)
                itemRepository.update(
                    id,
                    it.name,
                    it.description,
                    it.image,
                    it.imageData,
                )
            }

        updatedItems
            .filter { it.collection == null }  // Only stand-alone items
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
                itemRepository.update(
                    id,
                    it.numerator,
                    it.denominator,
                    it.destinationWorkchain,
                    it.destinationAddress,
                )
            }

        updatedItems.connect()
    }

    companion object : KLogging()
}
