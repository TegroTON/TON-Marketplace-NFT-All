package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.*
import money.tegro.market.nft.NFTCollection
import money.tegro.market.nft.NFTRoyalty
import money.tegro.market.ton.LiteApiFactory
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.ton.block.MsgAddressIntStd
import org.ton.boc.BagOfCells
import java.time.Instant
import javax.persistence.EntityManagerFactory

@Configuration
@EnableBatchProcessing
class CollectionJobs(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val entityManagerFactory: EntityManagerFactory,
    val liteApiFactory: LiteApiFactory,
    val collectionInfoRepository: CollectionInfoRepository,
    val collectionRoyaltyRepository: CollectionRoyaltyRepository,
) {
    @Bean
    fun requiredCollectionAddressReader() = FlatFileItemReaderBuilder<String>()
        .name("requiredCollectionReader")
        .resource(ClassPathResource("initial_collections.csv"))
        .delimited()
        .names("address")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

    @Bean
    fun collectionInfoReader() = JpaCursorItemReaderBuilder<CollectionInfo>()
        .name("collectionInfoReader")
        .queryString("from CollectionInfo")
        .entityManagerFactory(entityManagerFactory)
        .saveState(false)
        .build()

    @Bean
    fun collectionInfoWriter() = JpaItemWriterBuilder<CollectionInfo>()
        .entityManagerFactory(entityManagerFactory)
        .build()

    @Bean
    fun collectionRoyaltyWriter() = JpaItemWriterBuilder<CollectionRoyalty>()
        .entityManagerFactory(entityManagerFactory)
        .build()

    @Bean
    fun nftCollectionProcessor() = ItemProcessor<MsgAddressIntStd, NFTCollection> {
        runBlocking {
            NFTCollection.of(it, liteApiFactory.getObject())
        }
    }

    @Bean
    fun nftRoyaltyProcessor() = ItemProcessor<MsgAddressIntStd, Pair<MsgAddressIntStd, NFTRoyalty?>> {
        runBlocking {
            it to NFTRoyalty.of(it, liteApiFactory.getObject())
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
    fun collectionRoyaltyProcessor() = ItemProcessor<Pair<MsgAddressIntStd, NFTRoyalty?>, CollectionRoyalty> {
        val (address, royalty) = it
        collectionInfoRepository.findByAddress(address)?.let { collection ->
            (collectionRoyaltyRepository.findByCollection(collection)
                ?: CollectionRoyalty(collection)).apply {
                if (modified == null || numerator != royalty?.numerator || denominator != royalty?.denominator || destinationWorkchain != royalty?.destination?.workchainId || destinationAddress != royalty?.destination?.address?.toByteArray())
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
    fun initializeCollectionInfo() = stepBuilderFactory
        .get("initializeCollectionInfo")
        .chunk<String, CollectionInfo>(1)
        .processor(CompositeItemProcessor<String, CollectionInfo>().apply {
            setDelegates(
                arrayListOf(
                    ItemProcessor<String, MsgAddressIntStd> { MsgAddressIntStd(it) },
                    nftCollectionProcessor(),
                    collectionInfoProcessor()
                )
            )
        })
        .reader(requiredCollectionAddressReader())
        .writer(collectionInfoWriter())
        .build()

    @Bean
    fun updateCollectionInfo() = stepBuilderFactory
        .get("updateCollectionInfo")
        .chunk<CollectionInfo, CollectionInfo>(1)
        .processor(CompositeItemProcessor<CollectionInfo, CollectionInfo>().apply {
            setDelegates(
                arrayListOf(
                    ItemProcessor<CollectionInfo, MsgAddressIntStd> { it.address() },
                    nftCollectionProcessor(),
                    collectionInfoProcessor(),
                )
            )
        })
        .reader(collectionInfoReader())
        .writer(collectionInfoWriter())
        .build()

    @Bean
    fun updateCollectionRoyalty() = stepBuilderFactory
        .get("updateCollectionRoyalty")
        .chunk<CollectionInfo, CollectionRoyalty>(1)
        .processor(CompositeItemProcessor<CollectionInfo, CollectionRoyalty>().apply {
            setDelegates(
                arrayListOf(
                    ItemProcessor<CollectionInfo, MsgAddressIntStd> { it.address() },
                    nftRoyaltyProcessor(),
                    collectionRoyaltyProcessor(),
                )
            )
        })
        .reader(collectionInfoReader())
        .writer(collectionRoyaltyWriter())
        .build()

    @Bean
    fun initializeCollections() = jobBuilderFactory.get("initializeCollections")
        .start(initializeCollectionInfo())
        .next(updateCollectionRoyalty())
        .build()

    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(updateCollectionInfo())
        .next(updateCollectionRoyalty())
        .build()
}
