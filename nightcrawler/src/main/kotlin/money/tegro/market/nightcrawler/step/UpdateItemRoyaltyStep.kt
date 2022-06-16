package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemRoyalty
import money.tegro.market.nightcrawler.processor.ItemRoyaltyUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemRoyaltyAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateItemRoyaltyStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val itemInfoReader: ItemInfoReader,
    private val itemRoyaltyUpdateProcessor: ItemRoyaltyUpdateProcessor,
    private val itemRoyaltyAsyncWriter: ItemRoyaltyAsyncWriter,
) {
    @Bean
    fun updateItemRoyalty() = stepBuilderFactory
        .get("updateItemRoyalty")
        .chunk<ItemInfo, ItemRoyalty>(1)
        .processor(itemRoyaltyUpdateProcessor as ItemProcessor<in ItemInfo, out ItemRoyalty>)
        .reader(itemInfoReader)
        .writer(itemRoyaltyAsyncWriter as ItemWriter<in ItemRoyalty>)
        .build()
}
