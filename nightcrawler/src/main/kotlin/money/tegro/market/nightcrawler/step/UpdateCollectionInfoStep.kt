package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.nightcrawler.processor.CollectionInfoUpdateProcessor
import money.tegro.market.nightcrawler.reader.CollectionInfoReader
import money.tegro.market.nightcrawler.writer.CollectionInfoAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateCollectionInfoStep(
    private val stepBuilderFactory: StepBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val collectionInfoReader: CollectionInfoReader,
    private val collectionInfoUpdateProcessor: CollectionInfoUpdateProcessor,
    private val collectionInfoAsyncWriter: CollectionInfoAsyncWriter,
) {
    @Bean
    fun updateCollectionInfo() = stepBuilderFactory
        .get("updateCollectionInfo")
        .chunk<CollectionInfo, Future<CollectionInfo>>(1)
        .reader(collectionInfoReader)
        .processor(collectionInfoUpdateProcessor)
        .writer(collectionInfoAsyncWriter)
        .taskExecutor(taskExecutor)
        .build()
}
