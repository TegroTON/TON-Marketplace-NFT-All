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
            NFTCollection.of(it, liteApiFactory.getObject())
        }
    }

    @Bean
    fun nftItemProcessor() = ItemProcessor<MsgAddressIntStd, Pair<MsgAddressIntStd, NFTItem?>> {
        runBlocking {
            it to NFTItem.of(it, liteApiFactory.getObject())
        }
    }

    @Bean
    fun nftRoyaltyProcessor() = ItemProcessor<MsgAddressIntStd, Pair<MsgAddressIntStd, NFTRoyalty?>> {
        runBlocking {
            it to NFTRoyalty.of(it, liteApiFactory.getObject())
        }
    }

    @Bean
    fun nftCollectionMetadataProcessor() =
        ItemProcessor<CollectionInfo, Pair<MsgAddressIntStd, NFTMetadata?>> {
            runBlocking {
                it.addressStd() to it.content?.let { NFTMetadata.of(BagOfCells(it).roots.first()) }
            }
        }

    @Bean
    fun nftItemMetadataProcessor() =
        ItemProcessor<ItemInfo, Pair<MsgAddressIntStd, NFTMetadata?>> {
            runBlocking {
                Pair(
                    it.addressStd(),
                    it.collection?.let { collection ->
                        it.index?.let { index ->
                            it.content?.let { individualContent ->
                                NFTDeployedCollectionItem.contentOf(
                                    collection.addressStd(),
                                    index,
                                    BagOfCells(individualContent).roots.first(),
                                    liteApiFactory.getObject(),
                                ).let {
                                    NFTMetadata.of(it)
                                }
                            }
                        }
                    } ?: it.content?.let { BagOfCells(it).roots.first() }?.let { NFTMetadata.of(it) }
                )
            }
        }

    @Bean
    fun missingCollectionItemsProcessor() = ItemProcessor<CollectionInfo, List<ItemInfo>> { collection ->
        runBlocking {
            val addedIndices = collection.items.orEmpty().map { it.index }.filterNotNull()
            collection.nextItemIndex?.let { (0 until it) }
                ?.filter { !addedIndices.contains(it) }
                ?.map {
                    NFTDeployedCollection.itemAddressOf(collection.addressStd(), it, liteApiFactory.getObject())
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
    fun itemInfoProcessor() = ItemProcessor<Pair<MsgAddressIntStd, NFTItem?>, ItemInfo> {
        val (address, item) = it
        (itemInfoRepository.findByAddress(address) ?: ItemInfo(
            address.workchainId,
            address.address.toByteArray(),
        )).apply {
            if (modified == null ||
                initialized != (item != null) ||
                index != item?.index ||
                collection?.addressStd() != (item as? NFTDeployedCollectionItem)?.collection ||
                ownerWorkchain != item?.owner?.workchainId ||
                !ownerAddress.contentEquals(item?.owner?.address?.toByteArray()) ||
                !content.contentEquals(item?.individualContent?.let { BagOfCells(it) }?.toByteArray())
            )
                modified = Instant.now()

            initialized = item != null
            index = item?.index
            collection =
                (item as? NFTDeployedCollectionItem)?.collection?.let { collectionInfoRepository.findByAddress(it) }
            ownerWorkchain = item?.owner?.workchainId
            ownerAddress = item?.owner?.address?.toByteArray()
            content = item?.individualContent?.let { BagOfCells(it) }?.toByteArray()

            updated = Instant.now()
        }
    }

    @Bean
    fun collectionRoyaltyProcessor() = ItemProcessor<Pair<MsgAddressIntStd, NFTRoyalty?>, CollectionRoyalty> {
        val (address, royalty) = it
        collectionInfoRepository.findByAddress(address)?.let { collection ->
            (collectionRoyaltyRepository.findByCollection(collection)
                ?: CollectionRoyalty(collection)).apply {
                if (modified == null || numerator != royalty?.numerator || denominator != royalty?.denominator || destinationWorkchain != royalty?.destination?.workchainId || !destinationAddress.contentEquals(
                        royalty?.destination?.address?.toByteArray()
                    )
                )
                    modified = Instant.now()

                numerator = royalty?.numerator
                denominator = royalty?.denominator
                destinationWorkchain = royalty?.destination?.workchainId
                destinationAddress = royalty?.destination?.address?.toByteArray()
                updated = Instant.now()
            }
        }
    }

    @Bean
    fun collectionMetadataProcessor() = ItemProcessor<Pair<MsgAddressIntStd, NFTMetadata?>, CollectionMetadata> {
        val (address, metadata) = it
        collectionInfoRepository.findByAddress(address)?.let { collection ->
            (collectionMetadataRepository.findByCollection(collection)
                ?: CollectionMetadata(collection)).apply {
                if (modified == null ||
                    name != metadata?.name ||
                    description != metadata?.description ||
                    image != metadata?.image ||
                    !imageData.contentEquals(metadata?.imageData) ||
                    coverImage != metadata?.coverImage ||
                    !coverImageData.contentEquals(metadata?.coverImageData)
                )
                    modified = Instant.now()

                name = metadata?.name
                description = metadata?.description
                image = metadata?.image
                imageData = metadata?.imageData
                coverImage = metadata?.coverImage
                coverImageData = metadata?.coverImageData

                updated = Instant.now()
            }
        }
    }

    @Bean
    fun itemMetadataProcessor() = ItemProcessor<Pair<MsgAddressIntStd, NFTMetadata?>, ItemMetadata> {
        val (address, metadata) = it
        itemInfoRepository.findByAddress(address)?.let { item ->
            (itemMetadataRepository.findByItem(item)
                ?: ItemMetadata(item)).apply {
                if (modified == null ||
                    name != metadata?.name ||
                    description != metadata?.description ||
                    image != metadata?.image ||
                    !imageData.contentEquals(metadata?.imageData)
                )
                    modified = Instant.now()

                name = metadata?.name
                description = metadata?.description
                image = metadata?.image
                imageData = metadata?.imageData

                updated = Instant.now()
            }
        }
    }
}
