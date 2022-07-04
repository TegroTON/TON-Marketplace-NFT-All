package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.model.SaleModel
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import picocli.CommandLine
import picocli.CommandLine.Parameters

@CommandLine.Command(name = "query-item", description = ["Query api/blockchain for the information about NFT items"])
class QueryItemCommand : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @Inject
    private lateinit var client: ItemClient

    @Parameters(description = ["Addresses of the items to query"])
    private lateinit var addresses: List<String>

    override fun run() {
        runBlocking {
            for (addressStr in addresses) {
                val address = AddrStd(addressStr)
                println("Querying an NFT item ${address.toString(userFriendly = true)}")
                client.getItem(addressStr).awaitSingleOrNull()?.let {
                    println("Found in the Market API:")
                    println(ppDataClass(it))
                }
                queryBlockchain(address).awaitSingleOrNull()?.let {
                    println("Blockchain information:")
                    println(ppDataClass(it))
                }
            }
        }
    }

    fun queryBlockchain(address: AddrStd) = mono {
        val item = NFTItem.of(address, liteApi)
        val royalty = item.royalty(liteApi)
        val metadata = item.metadata(liteApi)
        val sale = item.sale(liteApi)

        ItemModel.of(item, metadata)?.let { model ->
            ItemDTO(
                model,
                sale?.let { SaleModel.of(it) },
                RoyaltyModel.of(royalty),
                metadata.attributes?.asSequence()?.map { AttributeModel(model.address, it) }?.asIterable()
            )
        }
    }
}
