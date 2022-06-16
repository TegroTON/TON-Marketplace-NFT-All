package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionRoyalty
import money.tegro.market.nightcrawler.processor.CollectionRoyaltyUpdateProcessor
import money.tegro.market.nightcrawler.reader.CollectionInfoReader
import money.tegro.market.nightcrawler.writer.CollectionRoyaltyAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class UpdateCollectionRoyaltyStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val collectionInfoReader: CollectionInfoReader,
    private val collectionRoyaltyUpdateProcessor: CollectionRoyaltyUpdateProcessor,
    private val collectionRoyaltyAsyncWriter: CollectionRoyaltyAsyncWriter,
) {
    @Bean
    fun updateCollectionRoyalty() = stepBuilderFactory
        .get("updateCollectionRoyalty")
        .chunk<CollectionInfo, CollectionRoyalty>(1)
        .processor(collectionRoyaltyUpdateProcessor as ItemProcessor<in CollectionInfo, out CollectionRoyalty>)
        .reader(collectionInfoReader)
        .writer(collectionRoyaltyAsyncWriter as ItemWriter<in CollectionRoyalty>)
        .build()
}
