package money.tegro.market.nightcrawler.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class PrimeCollectionsJob(
    private val jobBuilderFactory: JobBuilderFactory,

    private val primeCollectionAddresses: Step,
    private val updateCollectionInfo: Step,
    private val updateCollectionRoyalty: Step,
    private val updateCollectionMetadata: Step,
) {
    @Bean
    fun primeCollections() = jobBuilderFactory.get("primeCollections")
        .start(primeCollectionAddresses)
        .next(updateCollectionInfo)
        .next(updateCollectionRoyalty)
        .next(updateCollectionMetadata)
        .build()
}
