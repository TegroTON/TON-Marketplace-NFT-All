package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.*
import money.tegro.market.nft.NFTCollection
import money.tegro.market.nft.NFTItem
import money.tegro.market.nft.NFTRoyalty
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
    val itemInfoRepository: ItemInfoRepository,
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
    fun nftRoyaltyProcessor() = ItemProcessor<MsgAddressIntStd, NFTRoyalty> {
        runBlocking {
            NFTRoyalty.of(it, liteApiFactory.getObject(), liteApiFactory.lastMasterchainBlock)
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
}
