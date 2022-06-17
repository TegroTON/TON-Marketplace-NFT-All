package money.tegro.market.nightcrawler.job

import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.job.builder.FlowBuilder
import org.springframework.batch.core.job.flow.support.SimpleFlow
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableBatchProcessing
@EnableScheduling
class UpdateItemsJob(
    private val jobBuilderFactory: JobBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val updateItemInfo: Step,
    private val updateItemRoyalty: Step,
    private val updateItemMetadata: Step,
    private val updateItemSale: Step,
) {
    @Bean
    @Scheduled(initialDelay = 20_000L, fixedDelay = 60_000L)
    fun updateItems() = jobBuilderFactory.get("updateItems")
        .incrementer(RunIdIncrementer())
        .start(
            FlowBuilder<SimpleFlow>("updateItemInfoFlow")
                .start(updateItemInfo)
                .build()
        )
        .next(
            FlowBuilder<SimpleFlow>("parallelUpdateItemRoyaltyMetadataSaleFlow")
                .split(taskExecutor)
                .add(
                    FlowBuilder<SimpleFlow>("updateItemRoyaltyFlow")
                        .start(updateItemRoyalty)
                        .build(),
                    FlowBuilder<SimpleFlow>("updateItemMetadataFlow")
                        .start(updateItemMetadata)
                        .build(),
                    FlowBuilder<SimpleFlow>("updateItemSaleFlow")
                        .start(updateItemSale)
                        .build(),
                )
                .build()
        )
        .build()
        .build()
}
