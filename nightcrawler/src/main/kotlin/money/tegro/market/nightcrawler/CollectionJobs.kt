package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
@EnableBatchProcessing
class CollectionJobs(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val collectionInfoReader: CollectionInfoReader,
    val collectionInfoWriter: CollectionInfoWriter,
    val addressProcessor: AddressProcessor,
    val entityAddressProcessor: EntityAddressProcessor,
    val nftCollectionProcessor: NFTCollectionProcessor,
    val collectionInfoProcessor: CollectionInfoProcessor,
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
    fun initializeCollections() = jobBuilderFactory.get("initializeCollections")
        .start(initializeCollectionInfo())
        .build()

    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(updateCollectionInfo())
        .build()
}
