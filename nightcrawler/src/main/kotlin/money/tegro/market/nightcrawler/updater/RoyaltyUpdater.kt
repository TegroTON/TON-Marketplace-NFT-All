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

open class RoyaltyUpdater<M : RoyaltyModel>(
    private val liteApi: LiteApi,
) : java.util.function.Function<M, Publisher<M>> {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun apply(it: M): Publisher<M> =
        mono {
            val nftRoyalty = NFTRoyalty.of(it.address.to(), liteApi)

            it.apply {
                royalty = nftRoyalty?.let {
                    RoyaltyKey(it.numerator, it.denominator, AddressKey.of(it.destination))
                }
                royaltyModified = Instant.now() // TODO
            }
        }
}

@Singleton
class CollectionRoyaltyUpdater(liteApi: LiteApi) : RoyaltyUpdater<CollectionModel>(liteApi)

@Singleton
class ItemRoyaltyUpdater(liteApi: LiteApi) : RoyaltyUpdater<ItemModel>(liteApi)