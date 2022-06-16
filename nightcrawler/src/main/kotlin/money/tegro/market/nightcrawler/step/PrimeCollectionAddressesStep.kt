package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.nightcrawler.processor.PrimeCollectionInfoProcessor
import money.tegro.market.nightcrawler.writer.CollectionInfoWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
@EnableBatchProcessing
class PrimeCollectionAddressesStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val primeCollectionInfoProcessor: PrimeCollectionInfoProcessor,
    private val collectionInfoWriter: CollectionInfoWriter,
) {
    @Bean
    fun primeCollectionAddressesReader() = FlatFileItemReaderBuilder<String>()
        .name("initialCollectionReader")
        .resource(ClassPathResource("collection_primer.csv"))
        .delimited()
        .names("collection")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

    @Bean
    fun primeCollectionAddresses() = stepBuilderFactory
        .get("primeCollectionAddresses")
        .chunk<String, CollectionInfo>(1)
        .processor(primeCollectionInfoProcessor)
        .reader(primeCollectionAddressesReader())
        .writer(collectionInfoWriter)
        .build()
}
