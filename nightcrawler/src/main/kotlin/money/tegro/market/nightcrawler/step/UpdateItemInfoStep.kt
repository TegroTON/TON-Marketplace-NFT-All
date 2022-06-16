package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.nightcrawler.processor.ItemInfoUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemInfoAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateItemInfoStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val itemInfoReader: ItemInfoReader,
    private val itemInfoUpdateProcessor: ItemInfoUpdateProcessor,
    private val itemInfoAsyncWriter: ItemInfoAsyncWriter,
) {
    @Bean
    fun updateItemInfo() = stepBuilderFactory
        .get("updateItemInfo")
        .chunk<ItemInfo, ItemInfo>(1)
        .processor(itemInfoUpdateProcessor as ItemProcessor<in ItemInfo, out ItemInfo>)
        .reader(itemInfoReader)
        .writer(itemInfoAsyncWriter as ItemWriter<in ItemInfo>)
        .build()
}
