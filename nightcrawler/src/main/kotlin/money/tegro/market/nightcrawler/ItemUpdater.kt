package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.CollectionRepository
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

@Singleton
class ItemUpdater(
    private val liteApi: LiteApi,
    private val collectionRepository: CollectionRepository,
) : java.util.function.Function<ItemModel, Publisher<ItemModel>> {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun apply(it: ItemModel): Publisher<ItemModel> =
        mono {
            val nftItem = NFTItem.of(it.address.to(), liteApi)

            val new = it.copy().apply { // To check if anything was modified
                initialized = nftItem != null
                index = nftItem?.index
                collection = (nftItem as? NFTDeployedCollectionItem)?.collection?.let { AddressKey.of(it) }
                owner = nftItem?.owner?.let { AddressKey.of(it) }
                content = nftItem?.individualContent?.let { BagOfCells(it).toByteArray() }
            }

            if (new == it) it else new.apply { modified = Instant.now() }
        }
}
