package money.tegro.market.nightcrawler.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateCollectionsJob(
    private val jobBuilderFactory: JobBuilderFactory,

    private val updateCollectionInfo: Step,
    private val updateCollectionRoyalty: Step,
    private val updateCollectionMetadata: Step,
    private val discoverMissingCollectionItems: Step,
) {
    @Bean
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(updateCollectionInfo)
        .next(updateCollectionRoyalty)
        .next(updateCollectionMetadata)
        .next(discoverMissingCollectionItems)
        .build()
}
