package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionRoyalty
import money.tegro.market.nightcrawler.processor.CollectionRoyaltyUpdateProcessor
import money.tegro.market.nightcrawler.reader.CollectionInfoReader
import money.tegro.market.nightcrawler.writer.CollectionRoyaltyAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateCollectionRoyaltyStep(
    private val stepBuilderFactory: StepBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val collectionInfoReader: CollectionInfoReader,
    private val collectionRoyaltyUpdateProcessor: CollectionRoyaltyUpdateProcessor,
    private val collectionRoyaltyAsyncWriter: CollectionRoyaltyAsyncWriter,
) {
    @Bean
    fun updateCollectionRoyalty() = stepBuilderFactory
        .get("updateCollectionRoyalty")
        .chunk<CollectionInfo, Future<CollectionRoyalty>>(1)
        .reader(collectionInfoReader)
        .processor(collectionRoyaltyUpdateProcessor)
        .writer(collectionRoyaltyAsyncWriter)
        .taskExecutor(taskExecutor)
        .build()
}
