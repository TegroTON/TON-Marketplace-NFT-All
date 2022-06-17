package money.tegro.market.nightcrawler.step

import money.tegro.market.db.CollectionInfo
import money.tegro.market.db.CollectionMetadata
import money.tegro.market.nightcrawler.processor.CollectionMetadataUpdateProcessor
import money.tegro.market.nightcrawler.reader.CollectionInfoReader
import money.tegro.market.nightcrawler.writer.CollectionMetadataAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateCollectionMetadataStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val collectionInfoReader: CollectionInfoReader,
    private val collectionMetadataUpdateProcessor: CollectionMetadataUpdateProcessor,
    private val collectionMetadataAsyncWriter: CollectionMetadataAsyncWriter,
) {
    @Bean
    fun updateCollectionMetadata() = stepBuilderFactory
        .get("updateCollectionMetadata")
        .chunk<CollectionInfo, Future<CollectionMetadata>>(1)
        .processor(collectionMetadataUpdateProcessor)
        .reader(collectionInfoReader)
        .writer(collectionMetadataAsyncWriter)
        .build()
}
