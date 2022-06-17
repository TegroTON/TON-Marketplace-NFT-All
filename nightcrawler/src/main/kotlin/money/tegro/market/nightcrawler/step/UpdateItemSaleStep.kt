package money.tegro.market.nightcrawler.step

import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemSale
import money.tegro.market.nightcrawler.processor.ItemSaleUpdateProcessor
import money.tegro.market.nightcrawler.reader.ItemInfoReader
import money.tegro.market.nightcrawler.writer.ItemSaleAsyncWriter
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Future

@Configuration
@EnableBatchProcessing
class UpdateItemSaleStep(
    private val stepBuilderFactory: StepBuilderFactory,

    private val itemInfoReader: ItemInfoReader,
    private val itemSaleUpdateProcessor: ItemSaleUpdateProcessor,
    private val itemSaleAsyncWriter: ItemSaleAsyncWriter,
) {
    @Bean
    fun updateItemSale() = stepBuilderFactory
        .get("updateItemSale")
        .chunk<ItemInfo, Future<ItemSale>>(1)
        .processor(itemSaleUpdateProcessor)
        .reader(itemInfoReader)
        .writer(itemSaleAsyncWriter)
        .build()
}
