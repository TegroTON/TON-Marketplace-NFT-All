package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.nightcrawler.processor.PrimeItemInfoProcessor
import money.tegro.market.nightcrawler.writer.ItemInfoWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
@EnableBatchProcessing
class PrimeItemAddressesStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val primeItemInfoProcessor: PrimeItemInfoProcessor,
    private val itemInfoWriter: ItemInfoWriter,
) {
    @Bean
    fun primeItemAddressesReader() = FlatFileItemReaderBuilder<String>()
        .name("primeItemAddressesReader")
        .resource(ClassPathResource("item_primer.csv"))
        .delimited()
        .names("item")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

    @Bean
    fun primeItemAddresses() = stepBuilderFactory
        .get("primeItemAddresses")
        .chunk<String, ItemInfo>(1)
        .processor(primeItemInfoProcessor)
        .reader(primeItemAddressesReader())
        .writer(itemInfoWriter)
        .build()
}
