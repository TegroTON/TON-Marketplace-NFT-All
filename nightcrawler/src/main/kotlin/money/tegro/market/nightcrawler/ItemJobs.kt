package money.tegro.market.nightcrawler

import money.tegro.market.db.AddressableEntity
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemMetadata
import money.tegro.market.nft.NFTItem
import money.tegro.market.nft.NFTMetadata
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.MsgAddressIntStd

@Configuration
@EnableBatchProcessing
class ItemJobs(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val itemInfoReader: ItemReader<ItemInfo>,
    val itemInfoWriter: ItemWriter<ItemInfo>,
    val itemMetadataWriter: ItemWriter<ItemMetadata>,

    val entityAddressProcessor: ItemProcessor<AddressableEntity, MsgAddressIntStd>,

    val nftItemProcessor: ItemProcessor<MsgAddressIntStd, Pair<MsgAddressIntStd, NFTItem?>>,
    val itemInfoProcessor: ItemProcessor<Pair<MsgAddressIntStd, NFTItem?>, ItemInfo>,

    val nftItemMetadataProcessor: ItemProcessor<ItemInfo, Pair<MsgAddressIntStd, NFTMetadata?>>,
    val itemMetadataProcessor: ItemProcessor<Pair<MsgAddressIntStd, NFTMetadata?>, ItemMetadata>,
) {
    @Bean
    fun updateItemInfo() = stepBuilderFactory
        .get("updateItemInfo")
        .chunk<ItemInfo, ItemInfo>(1)
        .processor(CompositeItemProcessor<ItemInfo, ItemInfo>().apply {
            setDelegates(
                arrayListOf(
                    entityAddressProcessor,
                    nftItemProcessor,
                    itemInfoProcessor,
                )
            )
        })
        .reader(itemInfoReader)
        .writer(itemInfoWriter)
        .build()

    @Bean
    fun updateItemMetadata() = stepBuilderFactory
        .get("updateItemMetadata")
        .chunk<ItemInfo, ItemMetadata>(1)
        .processor(CompositeItemProcessor<ItemInfo, ItemMetadata>().apply {
            setDelegates(
                arrayListOf(
                    nftItemMetadataProcessor,
                    itemMetadataProcessor,
                )
            )
        })
        .reader(itemInfoReader)
        .writer(itemMetadataWriter)
        .build()

    @Bean
    fun updateItems() = jobBuilderFactory.get("updateItems")
        .incrementer(RunIdIncrementer())
        .start(updateItemInfo())
        .next(updateItemMetadata())
        .build()
}
