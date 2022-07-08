package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTException
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.dto.RoyaltyDTO
import money.tegro.market.core.dto.SaleDTO
import money.tegro.market.core.toSafeBounceable
import org.ton.block.AddrNone
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
        val metadata = item.metadata(liteApi)
        val royalty = try {
            if (item.collection is AddrNone) {
                item.royalty(liteApi)
            } else {
                NFTRoyalty.of(item.collection as AddrStd, liteApi)
            }
        } catch (e: NFTException) {
            null
        }
        val sale = try {
            item.sale(liteApi)
        } catch (e: NFTException) {
            null
        }

        ItemDTO(
            address = (item.address as AddrStd).toSafeBounceable(),
            index = item.index,
            collection = (item.collection as? AddrStd)?.toSafeBounceable(),
            owner = (item.owner as? AddrStd)?.toSafeBounceable(),
            name = metadata.name,
            description = metadata.description,
            sale = sale?.let {
                SaleDTO(
                    address = (it.address as AddrStd).toSafeBounceable(),
                    marketplace = it.marketplace.toSafeBounceable().orEmpty(),
                    item = it.item.toSafeBounceable().orEmpty(),
                    owner = it.owner.toSafeBounceable().orEmpty(),
                    fullPrice = it.fullPrice,
                    marketplaceFee = it.marketplaceFee,
                    royalty = it.royalty,
                    royaltyDestination = it.royaltyDestination.toSafeBounceable()
                )
            },
            royalty = royalty?.let {
                RoyaltyDTO(
                    it.value(),
                    (it.destination as? AddrStd)?.toSafeBounceable()
                )
            },
            attributes = metadata.attributes?.associate { it.trait to it.value } ?: mapOf(),
        )
    }
}
