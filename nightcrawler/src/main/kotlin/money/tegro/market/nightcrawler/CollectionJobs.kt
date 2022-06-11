package money.tegro.market.nightcrawler

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.CollectionInfo
import money.tegro.market.nft.NFTCollection
import org.hibernate.SessionFactory
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.HibernateItemWriterBuilder
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.transaction.annotation.Transactional
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@Configuration
@EnableBatchProcessing
class CollectionJobs(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val sessionFactory: SessionFactory,
    val liteApiFactory: LiteApiFactory,
    val collectionInfoRepository: CollectionInfoRepository,
) {
    @Bean
    fun requiredCollectionAddressReader() = FlatFileItemReaderBuilder<String>()
        .name("requiredCollectionReader")
        .resource(ClassPathResource("required_collections.csv"))
        .delimited()
        .names("address")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

    @Bean
    fun collectionInfoReader() = HibernateCursorItemReaderBuilder<CollectionInfo>()
        .name("collectionInfoReader")
        .sessionFactory(sessionFactory)
        .queryString("from CollectionInfo")
        .build()

    @Bean
    fun collectionInfoWriter() = HibernateItemWriterBuilder<CollectionInfo>()
        .sessionFactory(sessionFactory)
        .build()

    @Bean
    fun nftCollectionProcessor() = ItemProcessor<MsgAddressIntStd, NFTCollection> {
        runBlocking {
            NFTCollection.of(it, liteApiFactory.getObject())
        }
    }

    @Bean
    @Transactional(readOnly = true)
    fun collectionInfoProcessor() = ItemProcessor<NFTCollection, CollectionInfo> {
        val collection = collectionInfoRepository.findByAddress(it.address) ?: CollectionInfo(
            it.address.workchainId,
            it.address.address.toByteArray(),
        )
        collection.apply {
            nextItemIndex = it.nextItemIndex
            owner(it.owner)
            modified = Instant.now()
            updated = Instant.now()
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
                    collectionInfoProcessor()
                )
            )
        })
        .reader(collectionInfoReader())
        .writer(collectionInfoWriter())
        .build()

    @Bean
    fun initializeCollections() = jobBuilderFactory.get("initializeCollections")
        .start(initializeCollectionInfo())
        .build()

    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .start(updateCollectionInfo())
        .build()
}
