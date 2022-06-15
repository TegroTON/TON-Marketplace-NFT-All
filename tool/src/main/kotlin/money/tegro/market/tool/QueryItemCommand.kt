package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.*
import org.ton.block.MsgAddressIntStd
import org.ton.crypto.hex
import kotlin.math.pow

class QueryItemCommand :
    CliktCommand(name = "query", help = "Query blockchain for the information about NFT items") {
    private val addresses by argument(
        name = "addresses",
        help = "Addresses of the items to query"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            for (addressStr in addresses) {
                val address = MsgAddressIntStd(addressStr)
                println("Querying an NFT item ${address.toString(userFriendly = true)}")
                val item = NFTItem.of(address, Tool.currentLiteApi)
                println("\tInitialized? - ${item != null}")
                item?.run {
                    println("\tIndex: $index")
                    println("\tBelongs to a collection?: ${this is NFTDeployedCollectionItem}")
                    (this as? NFTDeployedCollectionItem)?.run {
                        println("\tCollection address: ${collection.toString(userFriendly = true)}")
                    }

                    println("\tOwner Address: ${owner.toString(userFriendly = true)}")

                    println("Querying ${if (this is NFTDeployedCollectionItem) "collection" else "item"} royalty information")
                    val royalty =
                        NFTRoyalty.of((this as? NFTDeployedCollectionItem)?.collection ?: address, Tool.currentLiteApi)
                    royalty?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    } ?: run {
                        println("\tNo royalty information")
                    }

                    println("Querying owner of this item")
                    NFTSale.of(owner, Tool.currentLiteApi)?.run {
                        println("\tOwner account ${this.address.toString(userFriendly = true)} implements an NFT-sale contract")
                        println("\tMarketplace: ${marketplace.toString(userFriendly = true)}")
                        println("\tSeller: ${owner.toString(userFriendly = true)}")
                        println("\tPrice: ${price.toFloat() / 10f.pow(9f)} TON ($price nanoTON)")
                        println("\tMarketplace fee:  ${marketplaceFee.toFloat() / 10f.pow(9f)} TON ($marketplaceFee nanoTON)")
                        println("\tRoyalties:  ${this.royalty?.toFloat()?.div(10f.pow(9f))} TON ($royalty nanoTON)")
                        println("\tRoyalty destination: ${royaltyDestination?.toString(userFriendly = true)}")
                    }

                    println("Querying item metadata")
                    NFTMetadata.of(content(Tool.currentLiteApi)).run {
                        println("\tName: $name")
                        println("\tDescription: $description")
                        println("\tImage: $image")
                        println("\tImage data: ${imageData?.let { hex(it) }}")
                        println("\tAttributes:")
                        attributes.orEmpty().forEach {
                            println("\t\t${it.trait}: ${it.value}")
                        }
                    }
                }
            }
        }
    }
}
