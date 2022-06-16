package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemMetadata
import money.tegro.market.nightcrawler.processor.ItemMetadataUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemMetadataAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateItemMetadataStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val itemInfoReader: ItemInfoReader,
    private val itemMetadataUpdateProcessor: ItemMetadataUpdateProcessor,
    private val itemMetadataAsyncWriter: ItemMetadataAsyncWriter,
) {
    @Bean
    fun updateItemMetadata() = stepBuilderFactory
        .get("updateItemMetadata")
        .chunk<ItemInfo, ItemMetadata>(1)
        .processor(itemMetadataUpdateProcessor as ItemProcessor<in ItemInfo, out ItemMetadata>)
        .reader(itemInfoReader)
        .writer(itemMetadataAsyncWriter as ItemWriter<in ItemMetadata>)
        .build()
}
