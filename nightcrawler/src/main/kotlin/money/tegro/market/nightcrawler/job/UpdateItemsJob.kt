package money.tegro.market.nightcrawler.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateItemsJob(
    private val jobBuilderFactory: JobBuilderFactory,

    private val updateItemInfo: Step,
    private val updateItemRoyalty: Step,
    private val updateItemMetadata: Step,
) {
    @Bean
    fun updateItems() = jobBuilderFactory.get("updateItems")
        .incrementer(RunIdIncrementer())
        .start(updateItemInfo)
        .next(updateItemRoyalty)
        .next(updateItemMetadata)
        .build()
}
