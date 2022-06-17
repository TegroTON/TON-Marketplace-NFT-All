package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.nightcrawler.processor.ItemInfoUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemInfoAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateItemInfoStep(
    private val stepBuilderFactory: StepBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val itemInfoReader: ItemInfoReader,
    private val itemInfoUpdateProcessor: ItemInfoUpdateProcessor,
    private val itemInfoAsyncWriter: ItemInfoAsyncWriter,
) {
    @Bean
    fun updateItemInfo() = stepBuilderFactory
        .get("updateItemInfo")
        .chunk<ItemInfo, Future<ItemInfo>>(1)
        .reader(itemInfoReader)
        .processor(itemInfoUpdateProcessor)
        .writer(itemInfoAsyncWriter)
        .taskExecutor(taskExecutor)
        .build()
}
