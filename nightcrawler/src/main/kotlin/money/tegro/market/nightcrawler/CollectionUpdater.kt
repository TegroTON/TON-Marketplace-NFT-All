package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.CollectionModel
import org.reactivestreams.Publisher
import org.ton.boc.BagOfCells
import org.ton.lite.api.LiteApi
import java.time.Instant

@Singleton
class CollectionUpdater(
    private val liteApi: LiteApi,
) : java.util.function.Function<CollectionModel, Publisher<CollectionModel>> {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun apply(it: CollectionModel): Publisher<CollectionModel> =
        mono {
            val nftCollection = NFTCollection.of(it.address.to(), liteApi)

            val new = it.copy().apply { // To check if anything was modified
                nextItemIndex = nftCollection.nextItemIndex
                owner = AddressKey.of(nftCollection.owner)
                content = BagOfCells(nftCollection.content).toByteArray()
            }

            if (new == it) it else new.apply { modified = Instant.now() }
        }
}
