package money.tegro.market.nightcrawler.processor

import kotlinx.coroutines.runBlocking
import money.tegro.market.db.ItemInfo
import money.tegro.market.db.ItemMetadata
import money.tegro.market.nft.NFTDeployedCollectionItem
import money.tegro.market.nft.NFTMetadata
import org.springframework.batch.integration.async.AsyncItemProcessor
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

class ItemMetadataUpdateProcessor(liteApi: LiteApi) : AsyncItemProcessor<ItemInfo, ItemMetadata>() {
    init {
        setDelegate { item ->
            runBlocking {
                val metadata = if (item.collection != null) {
                    item.collection?.let {
                        item.index?.let { index ->
                            item.content?.let { content ->
                                NFTMetadata.of(
                                    NFTDeployedCollectionItem.contentOf(
                                        it.addressStd(),
                                        index,
                                        BagOfCells(content).roots.first(),
                                        liteApi
                                    )
                                )
                            }
                        }
                    }
                } else {
                    item.content?.let { content ->
                        NFTMetadata.of(BagOfCells(content).roots.first())
                    }
                }

                (item.metadata ?: ItemMetadata(item)).apply {
                    item.metadata = this
                    updated = Instant.now()
                    cneq(this::name, metadata?.name)
                    cneq(this::description, metadata?.description)
                    cneq(this::image, metadata?.image)
                    cneq(this::imageData, metadata?.imageData)

//                    // TODO: Proper modification here, ugly for now
//                    if (attributes == null && metadata?.attributes.orEmpty().isNotEmpty()) {
//                        attributes = mutableSetOf()
//                    }
//
//                    metadata?.attributes.orEmpty()
//                        .forEach {
//                            attributes?.add(ItemAttribute(this, it.trait, it.value))
//                        }
                }
            }
        }
    }
}
