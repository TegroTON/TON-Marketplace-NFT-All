package money.tegro.market.nightcrawler

import money.tegro.market.db.AddressableEntity
import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionRoyalty
import money.tegro.market.db.ItemInfo
import money.tegro.market.nft.NFTCollection
import money.tegro.market.nft.NFTRoyalty
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.ton.block.MsgAddressIntStd

@Configuration
@EnableBatchProcessing
class CollectionJobs(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val collectionInfoReader: ItemReader<CollectionInfo>,
    val collectionInfoWriter: ItemWriter<CollectionInfo>,
    val collectionRoyaltyWriter: ItemWriter<CollectionRoyalty>,
    val itemInfoListWriter: ItemListWriter<ItemInfo>,

    val addressProcessor: ItemProcessor<String, MsgAddressIntStd>,
    val nftCollectionProcessor: ItemProcessor<MsgAddressIntStd, NFTCollection>,
    val collectionInfoProcessor: ItemProcessor<NFTCollection, CollectionInfo>,

    val nftRoyaltyProcessor: ItemProcessor<MsgAddressIntStd, NFTRoyalty>,

    val collectionRoyaltyProcessor: ItemProcessor<NFTRoyalty, CollectionRoyalty>,

    val entityAddressProcessor: ItemProcessor<AddressableEntity, MsgAddressIntStd>,
    val missingCollectionItemsProcessor: ItemProcessor<CollectionInfo, List<ItemInfo>>,
) {
    val initialCollectionsReader =
        ResourceLineReader("initialCollectionsReader", ClassPathResource("initial_collections.csv"))

    @Bean
    fun initializeCollectionInfo() = stepBuilderFactory
        .get("initializeCollectionInfo")
        .chunk<String, CollectionInfo>(1)
        .processor(CompositeItemProcessor<String, CollectionInfo>().apply {
            setDelegates(
                arrayListOf(
                    addressProcessor,
                    nftCollectionProcessor,
                    collectionInfoProcessor,
                )
            )
        })
        .reader(initialCollectionsReader)
        .writer(collectionInfoWriter)
        .build()

    @Bean
    fun updateCollectionInfo() = stepBuilderFactory
        .get("updateCollectionInfo")
        .chunk<CollectionInfo, CollectionInfo>(1)
        .processor(CompositeItemProcessor<CollectionInfo, CollectionInfo>().apply {
            setDelegates(
                arrayListOf(
                    entityAddressProcessor,
                    nftCollectionProcessor,
                    collectionInfoProcessor,
                )
            )
        })
        .reader(collectionInfoReader)
        .writer(collectionInfoWriter)
        .build()

    @Bean
    fun updateCollectionRoyalty() = stepBuilderFactory
        .get("updateCollectionRoyalty")
        .chunk<CollectionInfo, CollectionRoyalty>(1)
        .processor(CompositeItemProcessor<CollectionInfo, CollectionRoyalty>().apply {
            setDelegates(
                arrayListOf(
                    entityAddressProcessor,
                    nftRoyaltyProcessor,
                    collectionRoyaltyProcessor,
                )
            )
        })
        .reader(collectionInfoReader)
        .writer(collectionRoyaltyWriter)
        .build()

    @Bean
    fun discoverMissingItems() = stepBuilderFactory
        .get("discoverMissingItems")
        .chunk<CollectionInfo, List<ItemInfo>>(1)
        .processor(missingCollectionItemsProcessor)
        .reader(collectionInfoReader)
        .writer(itemInfoListWriter)
        .build()


    @Bean
    fun initializeCollections() = jobBuilderFactory.get("initializeCollections")
        .start(initializeCollectionInfo())
        .next(updateCollectionRoyalty())
        .next(discoverMissingItems())
        .build()

    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(updateCollectionInfo())
        .next(updateCollectionRoyalty())
        .next(discoverMissingItems())
        .build()
}
