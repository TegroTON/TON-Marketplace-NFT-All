package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.RoyaltyDTO
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.RoyaltyModel
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import picocli.CommandLine


@CommandLine.Command(
    name = "query-collection",
    description = ["Query api/blockchain for the information about NFT collections"]
)
class QueryCollectionCommand : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @Inject
    private lateinit var client: CollectionClient

    @CommandLine.Parameters(description = ["Addresses of the collections to query"])
    private lateinit var addresses: List<String>

    override fun run() {
        runBlocking {
            for (addressStr in addresses) {
                val address = AddrStd(addressStr)
                println("Querying an NFT collection ${address.toString(userFriendly = true)}")
                client.getCollection(addressStr).awaitSingleOrNull()?.let {
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
        val collection = NFTCollection.of(address, liteApi)
        val royalty = collection.royalty(liteApi)
        val metadata = collection.metadata()

        CollectionDTO(
            address = collection.address.toSafeBounceable().orEmpty(),
            size = collection.nextItemIndex,
            owner = collection.owner.toSafeBounceable(),
            name = metadata.name,
            description = metadata.description,
            royalty = RoyaltyModel.of(royalty)?.let { RoyaltyDTO(it) }
        )
    }
}
