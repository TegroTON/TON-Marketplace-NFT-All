package money.tegro.market.nightcrawler

import money.tegro.market.db.*
import money.tegro.market.nft.NFTCollection
import money.tegro.market.nft.NFTCollectionMetadata
import money.tegro.market.nft.NFTRoyalty
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
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

    val nftCollectionMetadataProcessor: ItemProcessor<CollectionInfo, NFTCollectionMetadata>,
    val collectionMetadataProcessor: ItemProcessor<NFTCollectionMetadata, CollectionMetadata>,

    val collectionMetadataWriter: ItemWriter<CollectionMetadata>,
) {
    @Bean
    fun initialCollectionsReader() = FlatFileItemReaderBuilder<String>()
        .name("initialCollectionReader")
        .resource(ClassPathResource("initial_collections.csv"))
        .delimited()
        .names("item")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

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
        .reader(initialCollectionsReader())
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
    fun updateCollectionMetadata() = stepBuilderFactory
        .get("updateCollectionMetadata")
        .chunk<CollectionInfo, CollectionMetadata>(1)
        .processor(CompositeItemProcessor<CollectionInfo, CollectionMetadata>().apply {
            setDelegates(
                arrayListOf(
                    nftCollectionMetadataProcessor,
                    collectionMetadataProcessor,
                )
            )
        })
        .reader(collectionInfoReader)
        .writer(collectionMetadataWriter)
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
        .next(updateCollectionMetadata())
        .next(discoverMissingItems())
        .build()

    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(updateCollectionInfo())
        .next(updateCollectionRoyalty())
        .next(updateCollectionMetadata())
        .next(discoverMissingItems())
        .build()
}
