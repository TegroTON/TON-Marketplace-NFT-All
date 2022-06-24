package money.tegro.market.nightcrawler.updater

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import org.reactivestreams.Publisher
import org.ton.block.MsgAddressIntStd
import org.ton.lite.api.LiteApi

@Singleton
class SaleUpdater(
    private val liteApi: LiteApi,
) : java.util.function.Function<MsgAddressIntStd, Publisher<SaleModel>> {
    init {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
        }
    }

    override fun apply(it: MsgAddressIntStd): Publisher<SaleModel> =
        mono {
            NFTSale.of(it, liteApi)?.let { nftSale ->
                SaleModel(
                    address = AddressKey.of(nftSale.address),
                    marketplace = AddressKey.of(nftSale.marketplace),
                    item = AddressKey.of(nftSale.item),
                    owner = AddressKey.of(nftSale.owner),
                    price = nftSale.price,
                    marketplaceFee = nftSale.marketplaceFee,
                    royalty = nftSale.royalty,
                    royaltyDestination = nftSale.royaltyDestination?.let { AddressKey.of(it) }
                )
            }
        }
}
