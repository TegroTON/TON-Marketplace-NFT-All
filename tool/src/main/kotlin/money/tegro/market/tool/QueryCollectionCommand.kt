package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTMetadata
import money.tegro.market.blockchain.nft.NFTRoyalty
import org.ton.block.MsgAddressIntStd
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import picocli.CommandLine


@CommandLine.Command(
    name = "query-collection",
    description = ["Query api/blockchain for the information about NFT collections"]
)
class QueryCollectionCommand : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @CommandLine.Parameters(description = ["Addresses of the collections to query"])
    private lateinit var addresses: List<String>

    override fun run() {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
            for (addressStr in addresses) {
                val address = MsgAddressIntStd(addressStr)
                println("Querying an NFT collection ${address.toString(userFriendly = true)}")
                val collection = NFTCollection.of(address, liteApi)
                collection.run {
                    println("\tNext item index (number of items in this collection): $nextItemIndex")
                    println("\tOwner: ${owner.toString(userFriendly = true)}")

                    println("Querying royalty information")
                    val royalty = NFTRoyalty.of(address, liteApi)
                    royalty?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    } ?: run {
                        println("\tNo royalty information")
                    }

                    println("Querying collection metadata")
                    NFTMetadata.of(content).run {
                        println("\tName: $name")
                        println("\tDescription: $description")
                        println("\tImage: $image")
                        println("\tImage data: ${imageData?.let { hex(it) }}")
                        println("\tCover image: $coverImage")
                        println("\tCover image data: ${coverImageData?.let { hex(it) }}")
                    }
                }
            }
        }
    }
}
