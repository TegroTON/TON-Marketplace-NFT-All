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
class UpdateCollectionsJob(
    private val jobBuilderFactory: JobBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val updateCollectionInfo: Step,
    private val updateCollectionRoyalty: Step,
    private val updateCollectionMetadata: Step,
    private val discoverMissingCollectionItems: Step,
) {
    @Bean
    @Scheduled(initialDelay = 10_000L, fixedDelay = 60_000L)
    fun updateCollections() = jobBuilderFactory.get("updateCollections")
        .incrementer(RunIdIncrementer())
        .start(
            FlowBuilder<SimpleFlow>("updateCollectionInfoFlow")
                .start(updateCollectionInfo)
                .build()
        )
        .next(
            FlowBuilder<SimpleFlow>("parallelUpdateCollectionRoyaltyMetadataItemsFlow")
                .split(taskExecutor)
                .add(
                    FlowBuilder<SimpleFlow>("updateCollectionRoyaltyFlow")
                        .start(updateCollectionRoyalty)
                        .build(),
                    FlowBuilder<SimpleFlow>("updateCollectionMetadataFlow")
                        .start(updateCollectionMetadata)
                        .build(),
                    FlowBuilder<SimpleFlow>("discoverMissingCollectionItemsFlow")
                        .start(discoverMissingCollectionItems)
                        .build(),
                )
                .build()
        )
        .build()
        .build()
}
