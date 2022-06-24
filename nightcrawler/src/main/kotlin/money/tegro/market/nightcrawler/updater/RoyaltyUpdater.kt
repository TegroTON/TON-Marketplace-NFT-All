package money.tegro.market.nightcrawler.updater

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.key.RoyaltyKey
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.RoyaltyModel
import org.reactivestreams.Publisher
import org.ton.lite.api.LiteApi
import java.time.Instant

abstract class RoyaltyUpdater<M : RoyaltyModel>(
    private val liteApi: LiteApi,
) : java.util.function.Function<M, Publisher<M>> {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    abstract fun copy(it: M): M // Similar to what we do in MetadataUpdater

    override fun apply(it: M): Publisher<M> =
        mono {
            val nftRoyalty = NFTRoyalty.of(it.address.to(), liteApi)

            val new = copy(it).apply {
                royalty = nftRoyalty?.let {
                    RoyaltyKey(it.numerator, it.denominator, AddressKey.of(it.destination))
                }
            }

            if (new == it) it else new.apply { royaltyModified = Instant.now() }
        }
}

@Singleton
class CollectionRoyaltyUpdater(liteApi: LiteApi) : RoyaltyUpdater<CollectionModel>(liteApi) {
    override fun copy(it: CollectionModel): CollectionModel = it.copy()
}

@Singleton
class ItemRoyaltyUpdater(liteApi: LiteApi) : RoyaltyUpdater<ItemModel>(liteApi) {
    override fun copy(it: ItemModel): ItemModel = it.copy()
}
