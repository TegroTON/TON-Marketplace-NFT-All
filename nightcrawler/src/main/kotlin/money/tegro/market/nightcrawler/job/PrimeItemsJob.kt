package money.tegro.market.nightcrawler.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class PrimeItemsJob(
    private val jobBuilderFactory: JobBuilderFactory,

    private val primeItemAddresses: Step,
    private val updateItemInfo: Step,
) {
    @Bean
    fun primeItems() = jobBuilderFactory.get("primeItems")
        .start(primeItemAddresses)
        .next(updateItemInfo)
        .build()
}
