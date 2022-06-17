package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemRoyalty
import money.tegro.market.nightcrawler.processor.ItemRoyaltyUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemRoyaltyAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateItemRoyaltyStep(
    private val stepBuilderFactory: StepBuilderFactory,
    private val taskExecutor: TaskExecutor,

    private val itemInfoReader: ItemInfoReader,
    private val itemRoyaltyUpdateProcessor: ItemRoyaltyUpdateProcessor,
    private val itemRoyaltyAsyncWriter: ItemRoyaltyAsyncWriter,
) {
    @Bean
    fun updateItemRoyalty() = stepBuilderFactory
        .get("updateItemRoyalty")
        .chunk<ItemInfo, Future<ItemRoyalty>>(1)
        .reader(itemInfoReader)
        .processor(itemRoyaltyUpdateProcessor)
        .writer(itemRoyaltyAsyncWriter)
        .taskExecutor(taskExecutor)
        .build()
}
