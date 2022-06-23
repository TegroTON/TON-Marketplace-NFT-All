package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.*
import org.ton.block.MsgAddressIntStd
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import picocli.CommandLine
import picocli.CommandLine.Parameters
import kotlin.math.pow

@CommandLine.Command(name = "query-item", description = ["Query api/blockchain for the information about NFT items"])
class QueryItemCommand : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @Parameters(description = ["Addresses of the items to query"])
    private lateinit var addresses: List<String>

    override fun run() {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
            for (addressStr in addresses) {
                val address = MsgAddressIntStd(addressStr)
                println("Querying an NFT item ${address.toString(userFriendly = true)}")
                val item = NFTItem.of(address, liteApi)
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
                        NFTRoyalty.of(
                            (this as? NFTDeployedCollectionItem)?.collection ?: address,
                            liteApi
                        )
                    royalty?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    } ?: run {
                        println("\tNo royalty information")
                    }

                    println("Querying owner of this item")
                    NFTSale.of(owner, liteApi)?.run {
                        println("\tOwner account ${this.address.toString(userFriendly = true)} implements an NFT-sale contract")
                        println("\tMarketplace: ${marketplace.toString(userFriendly = true)}")
                        println("\tSeller: ${owner.toString(userFriendly = true)}")
                        println("\tPrice: ${price.toFloat() / 10f.pow(9f)} TON ($price nanoTON)")
                        println("\tMarketplace fee:  ${marketplaceFee.toFloat() / 10f.pow(9f)} TON ($marketplaceFee nanoTON)")
                        println("\tRoyalties:  ${this.royalty?.toFloat()?.div(10f.pow(9f))} TON ($royalty nanoTON)")
                        println("\tRoyalty destination: ${royaltyDestination?.toString(userFriendly = true)}")
                    }

                    println("Querying item metadata")
                    NFTMetadata.of(content(liteApi)).run {
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
