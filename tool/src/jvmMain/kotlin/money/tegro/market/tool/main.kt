package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.IPFSConfiguration
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.*
import mu.KLogging
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.conf.ConfigurableDI
import org.kodein.di.instance
import org.ton.block.MsgAddressIntStd
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import org.ton.lite.client.LiteClient

class LiteServerOptions : OptionGroup("lite server options") {
    val host by option("--lite-server-host", help = "Lite server host IP address", envvar = "LITE_SERVER_HOST")
        .int()
        .default(84478479)
    val port by option("--lite-server-port", help = "Lite server port number", envvar = "LITE_SERVER_PORT")
        .int()
        .default(48014)
    val publicKey by option(
        "--lite-server-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_PUBLIC_KEY"
    )
        .default("3XO67K/qi+gu3T9v8G2hx1yNmWZhccL3O7SoosFo8G0=")
}

class IPFSOptions : OptionGroup("IPFS options") {
    val url by option("--ipfs-url", help = "IPFS API server url", envvar = "IPFS_URL")
        .default("http://127.0.0.1:5001/api/v0/")
}

class Tool(override val di: ConfigurableDI) :
    CliktCommand(name = "tool", help = "Your one-stop NFT item/collection shop"),
    DIAware {
    private val liteServerOptions by LiteServerOptions()
    private val ipfsOptions by IPFSOptions()

    override fun run() {
        runBlocking {
            di.addConfig {
                bindSingleton<LiteApi> {
                    LiteClient(
                        liteServerOptions.host,
                        liteServerOptions.port,
                        liteServerOptions.publicKey.let { base64(it) })
                }
                bindSingleton { IPFS(IPFSConfiguration(ipfsOptions.url)) }
            }

            val liteClient: LiteApi by instance()

            logger.debug("connecting to the lite client at ${liteServerOptions.host}:${liteServerOptions.port}")
            (liteClient as LiteClient).connect()

            val ipfs: IPFS by instance()

            logger.debug("IPFS API is initialized")
        }
    }

    companion object : KLogging()
}

class QueryItem(override val di: DI) : CliktCommand(name = "query-item", help = "Query NFT item info"), DIAware {
    val address by argument(name = "address", help = "NFT item contract address")
    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val item = liteClient.getNFTItem(MsgAddressIntStd.parse(address))
            println("NFT Item ${item.address.toString(userFriendly = true)}:")
            println("\tInitialized: ${item is NFTItemInitialized}")
            if (item is NFTItemInitialized) {
                println("\tIndex: ${item.index}")
                println("\tCollection Address: ${item.collection?.toString(userFriendly = true)}")
                println("\tOwner Address: ${item.owner.toString(userFriendly = true)}")

                (item.collection?.let { liteClient.getNFTCollectionRoyalties(it) }
                    ?: liteClient.getNFTItemRoyalties(item.address))
                    ?.let { royalties ->
                        println("\tRoyalty percentage: ${royalties.first.toFloat() * 100.0 / royalties.second}%")
                        println("\tRoyalty destination: ${royalties.third.toString(userFriendly = true)}")
                    }
            }
        }

    }
}

class QueryCollection(override val di: DI) :
    CliktCommand(name = "query-collection", help = "Query NFT collection info"), DIAware {
    val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val collection = liteClient.getNFTCollection(MsgAddressIntStd.parse(address))
            println("NFT Collection ${collection.address.toString(userFriendly = true)}")
            println("\tNumber of items: ${collection.size}")
            println("\tOwner address: ${collection.owner.toString(userFriendly = true)}")

            liteClient.getNFTCollectionRoyalties(collection.address)?.let {
                println("\tRoyalty percentage: ${it.first.toFloat() * 100.0 / it.second}%")
                println("\tRoyalty destination: ${it.third.toString(userFriendly = true)}")
            }
        }
    }
}

class ListCollection(override val di: DI) :
    CliktCommand(name = "list-collection", help = "List all items of the given NFT collection"),
    DIAware {
    val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val collection = liteClient.getNFTCollection(MsgAddressIntStd.parse(address))

            println("index | address | owner")

            for (i in 0 until collection.size) {
                val item = liteClient.getNFTCollectionItem(collection, i)
                if (item is NFTItemInitialized)
                    println(
                        "${item.index} | ${item.address.toString(userFriendly = true)} | ${
                            item.owner.toString(
                                userFriendly = true
                            )
                        }"
                    )
            }
        }
    }
}

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(QueryItem(di), QueryCollection(di), ListCollection(di)).main(args)
}
