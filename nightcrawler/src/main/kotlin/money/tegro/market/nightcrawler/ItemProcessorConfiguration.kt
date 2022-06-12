package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.*
import money.tegro.market.nft.*
import money.tegro.market.ton.LiteApiFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd
import org.ton.boc.BagOfCells
import java.time.Instant

@Configuration
class ItemProcessorConfiguration(
    val liteApiFactory: LiteApiFactory,
    val collectionInfoRepository: CollectionInfoRepository,
    val collectionRoyaltyRepository: CollectionRoyaltyRepository,
    val collectionMetadataRepository: CollectionMetadataRepository,
    val itemInfoRepository: ItemInfoRepository,
    val itemMetadataRepository: ItemMetadataRepository,
) {
    @Bean
    fun addressProcessor() = ItemProcessor<String, MsgAddressIntStd> { MsgAddressIntStd(it) }

    @Bean
    fun entityAddressProcessor() = ItemProcessor<AddressableEntity, MsgAddressIntStd> { it.addressStd() }

    @Bean
    fun nftCollectionProcessor() = ItemProcessor<MsgAddressIntStd, NFTCollection> {
        runBlocking {
            NFTCollection.of(it, liteApiFactory.getObject(), liteApiFactory.lastMasterchainBlock)
        }
    }

    @Bean
    fun nftItemProcessor() = ItemProcessor<MsgAddressIntStd, NFTItem> {
        runBlocking {
            NFTItem.of(it, liteApiFactory.getObject(), liteApiFactory.lastMasterchainBlock)
        }
    }

    @Bean
    fun nftRoyaltyProcessor() = ItemProcessor<MsgAddressIntStd, NFTRoyalty> {
        runBlocking {
            NFTRoyalty.of(it, liteApiFactory.getObject(), liteApiFactory.lastMasterchainBlock)
        }
    }

    @Bean
    fun nftCollectionMetadataProcessor() =
        ItemProcessor<CollectionInfo, NFTCollectionMetadata> {
            runBlocking {
                it.content?.let { content ->
                    NFTMetadata.of(
                        it.addressStd(),
                        BagOfCells(content).roots.first()
                    )
                }
            }
        }

    @Bean
    fun nftItemMetadataProcessor() =
        ItemProcessor<ItemInfo, NFTItemMetadata> {
            runBlocking {
                if (it.initialized) {
                    val content = it.collection?.let { collection ->
                        it.index?.let { index ->
                            it.content?.let { individualContent ->
                                NFTItemInitialized.content(
                                    collection.addressStd(),
                                    index,
                                    BagOfCells(individualContent).roots.first(),
                                    liteApiFactory.getObject(),
                                    liteApiFactory.lastMasterchainBlock
                                )
                            }
                        }
                    } ?: it.content?.let { BagOfCells(it) }?.roots?.first()

                    content?.let { c ->
                        NFTMetadata.of(it.addressStd(), c)
                    }
                } else {
                    null
                }
            }
        }

    @Bean
    fun missingCollectionItemsProcessor() = ItemProcessor<CollectionInfo, List<ItemInfo>> { collection ->
        runBlocking {
            val addedIndices = collection.items.orEmpty().map { it.index }.filterNotNull()
            collection.nextItemIndex?.let { (0 until it) }
                ?.filter { !addedIndices.contains(it) }
                ?.map {
                    NFTItem.of(collection.addressStd(), it, liteApiFactory.getObject())
                }
                ?.map {
                    (itemInfoRepository.findByAddress(it) ?: ItemInfo(
                        it.workchainId,
                        it.address.toByteArray(),
                    )).apply {
                        this.setCollection(collection)
                    }
                }
                ?.toList()
        }
    }

    @Bean
    fun collectionInfoProcessor() = ItemProcessor<NFTCollection, CollectionInfo> {
        (collectionInfoRepository.findByAddress(it.address) ?: CollectionInfo(
            it.address.workchainId,
            it.address.address.toByteArray(),
        )).apply {
            if (modified == null || nextItemIndex != it.nextItemIndex || !content.contentEquals(BagOfCells(it.content).toByteArray()) || owner() != it.owner)
                modified = Instant.now()

            nextItemIndex = it.nextItemIndex
            content = BagOfCells(it.content).toByteArray()
            owner(it.owner)
            updated = Instant.now()
        }
    }

    @Bean
    fun itemInfoProcessor() = ItemProcessor<NFTItem, ItemInfo> {
        (itemInfoRepository.findByAddress(it.address) ?: ItemInfo(
            it.address.workchainId,
            it.address.address.toByteArray(),
        )).apply {
            if (modified == null ||
                initialized != (it is NFTItemInitialized) ||
                index != (it as? NFTItemInitialized)?.index ||
                collection?.addressStd() != (it as? NFTItemInitialized)?.collection ||
                ownerWorkchain != (it as? NFTItemInitialized)?.owner?.workchainId ||
                !ownerAddress.contentEquals((it as? NFTItemInitialized)?.owner?.address?.toByteArray()) ||
                !content.contentEquals((it as? NFTItemInitialized)?.content?.let { BagOfCells(it) }?.toByteArray())
            )
                modified = Instant.now()

            initialized = (it is NFTItemInitialized)
            index = (it as? NFTItemInitialized)?.index
            collection = (it as? NFTItemInitialized)?.collection?.let { collectionInfoRepository.findByAddress(it) }
            ownerWorkchain = (it as? NFTItemInitialized)?.owner?.workchainId
            ownerAddress = (it as? NFTItemInitialized)?.owner?.address?.toByteArray()
            content = (it as? NFTItemInitialized)?.content?.let { BagOfCells(it) }?.toByteArray()

            updated = Instant.now()
        }
    }

    @Bean
    fun collectionRoyaltyProcessor() = ItemProcessor<NFTRoyalty, CollectionRoyalty> {
        collectionInfoRepository.findByAddress(it.address)?.let { collection ->
            (collectionRoyaltyRepository.findByCollection(collection)
                ?: CollectionRoyalty(collection)).apply {
                if (modified == null || numerator != it.numerator || denominator != it.denominator || destinationWorkchain != it.destination?.workchainId || !destinationAddress.contentEquals(
                        it.destination?.address?.toByteArray()
                    )
                )
                    modified = Instant.now()

                numerator = it.numerator
                denominator = it.denominator
                destinationWorkchain = it.destination?.workchainId
                destinationAddress = it.destination?.address?.toByteArray()
                updated = Instant.now()
            }
        }
    }

    @Bean
    fun collectionMetadataProcessor() = ItemProcessor<NFTCollectionMetadata, CollectionMetadata> {
        collectionInfoRepository.findByAddress(it.address)?.let { collection ->
            (collectionMetadataRepository.findByCollection(collection)
                ?: CollectionMetadata(collection)).apply {
                if (modified == null ||
                    name != it.name ||
                    description != it.description ||
                    image != it.image ||
                    !imageData.contentEquals(it.imageData) ||
                    coverImage != it.coverImage ||
                    !coverImageData.contentEquals(it.coverImageData)
                )
                    modified = Instant.now()

                name = it.name
                description = it.description
                image = it.image
                imageData = it.imageData
                coverImage = it.coverImage
                coverImageData = it.coverImageData

                updated = Instant.now()
            }
        }
    }

    @Bean
    fun itemMetadataProcessor() = ItemProcessor<NFTItemMetadata, ItemMetadata> {
        itemInfoRepository.findByAddress(it.address)?.let { item ->
            (itemMetadataRepository.findByItem(item)
                ?: ItemMetadata(item)).apply {
                if (modified == null ||
                    name != it.name ||
                    description != it.description ||
                    image != it.image ||
                    !imageData.contentEquals(it.imageData)
                )
                    modified = Instant.now()

                name = it.name
                description = it.description
                image = it.image
                imageData = it.imageData

                updated = Instant.now()
            }
        }
    }
}
