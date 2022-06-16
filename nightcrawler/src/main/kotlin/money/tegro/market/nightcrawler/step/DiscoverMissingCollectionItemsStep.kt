package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.ItemInfo
import money.tegro.market.nightcrawler.processor.CollectionMissingItemsProcessor
import money.tegro.market.nightcrawler.reader.CollectionInfoReader
import money.tegro.market.nightcrawler.writer.AsyncListWriter
import money.tegro.market.nightcrawler.writer.CollectionInfoAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class DiscoverMissingCollectionItemsStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val collectionInfoReader: CollectionInfoReader,
    private val collectionMissingItemsProcessor: CollectionMissingItemsProcessor,
    private val collectionInfoAsyncWriter: CollectionInfoAsyncWriter,
    private val itemInfoAsyncListWriter: AsyncListWriter<ItemInfo>,
) {
    @Bean
    fun discoverMissingCollectionItems() = stepBuilderFactory
        .get("discoverMissingCollectionItems")
        .chunk<CollectionInfo, List<ItemInfo>>(1)
        .processor(collectionMissingItemsProcessor as ItemProcessor<in CollectionInfo, out List<ItemInfo>>)
        .reader(collectionInfoReader)
        .writer(itemInfoAsyncListWriter as ItemWriter<in List<ItemInfo>>)
        .build()
}
