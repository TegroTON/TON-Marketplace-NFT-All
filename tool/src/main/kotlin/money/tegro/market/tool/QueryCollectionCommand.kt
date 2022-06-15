package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTCollection
import money.tegro.market.nft.NFTMetadata
import money.tegro.market.nft.NFTRoyalty
import org.ton.block.MsgAddressIntStd
import org.ton.crypto.hex

class QueryCollectionCommand :
    CliktCommand(name = "query", help = "Query blockchain for the information about NFT collections") {
    private val addresses by argument(
        name = "addresses",
        help = "Addresses of the collections to query"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            for (addressStr in addresses) {
                val address = MsgAddressIntStd(addressStr)
                println("Querying an NFT collection ${address.toString(userFriendly = true)}")
                val collection = NFTCollection.of(address, Tool.currentLiteApi)
                collection.run {
                    println("\tNext item index (number of items in this collection): $nextItemIndex")
                    println("\tOwner: ${owner.toString(userFriendly = true)}")

                    println("Querying royalty information")
                    val royalty = NFTRoyalty.of(address, Tool.currentLiteApi)
                    royalty?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    } ?: run {
                        println("\tNo royalty information")
                    }

                    println("Querying collection metadata")
                    NFTMetadata.of(content).run {
                        println("\tName: ${name}")
                        println("\tDescription: ${description}")
                        println("\tImage: ${image}")
                        println("\tImage data: ${imageData?.let { hex(it) }}")
                        println("\tCover image: ${coverImage}")
                        println("\tCover image data: ${coverImageData?.let { hex(it) }}")
                    }
                }
            }
        }
    }
}
