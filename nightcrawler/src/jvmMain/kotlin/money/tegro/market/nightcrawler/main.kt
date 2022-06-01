package money.tegro.market.nightcrawler

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.IPFSConfiguration
import kotlinx.coroutines.runBlocking
import money.tegro.market.db.Collections
import money.tegro.market.db.Item
import money.tegro.market.db.Items
import money.tegro.market.nft.NFTItemInitialized
import money.tegro.market.nft.getNFTCollection
import money.tegro.market.nft.getNFTCollectionRoyalties
import money.tegro.market.nft.getNFTItem
import mu.KLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.conf.ConfigurableDI
import org.kodein.di.instance
import org.ton.block.MsgAddressInt
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

class DatabaseOptions : OptionGroup("Database options") {
    val url by option("--db-url", help = "Database url", envvar = "DB_URL")
        .default("jdbc:sqlite:../build/nighcrawler.db")
    val driver by option("--db-driver", help = "Database driver", envvar = "DB_DRIVER")
        .default("org.sqlite.JDBC")
}

class Tool(override val di: ConfigurableDI) :
    CliktCommand(name = "nightcrawler", help = "Remember, use your zoom, steady hands."),
    DIAware {
    private val liteServerOptions by LiteServerOptions()
    private val ipfsOptions by IPFSOptions()
    private val databaseOptions by DatabaseOptions()

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
                bindSingleton { Database.connect(databaseOptions.url, databaseOptions.driver) }
            }

            val liteClient: LiteApi by instance()

            logger.debug("connecting to the lite client at ${liteServerOptions.host}:${liteServerOptions.port}")
            (liteClient as LiteClient).connect()

            val ipfs: IPFS by instance()

            logger.debug("initializing IPFS API")

            val db: Database by instance()

            logger.debug("connecting to the database ${db.vendor} v${db.version}")

            transaction {
                SchemaUtils.create(Collections, Items)
            }
        }
    }

    companion object : KLogging()
}

class AddCollection(override val di: DI) :
    CliktCommand(name = "add-collection", help = "Adds a collection to the database. Updates existing entries"),
    DIAware {
    val addresses by argument(
        name = "addresses",
        help = "NFT collection contract address(es)"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val database: Database by instance()

            addresses.forEach { address ->
                val collection = liteClient.getNFTCollection(MsgAddressInt.AddrStd.parse(address))

                newSuspendedTransaction {
                    val dbCollection =
                        money.tegro.market.db.Collection.find {
                            (Collections.workchain eq collection.address.workchainId) and (Collections.address eq collection.address.address)
                        }.firstOrNull() ?: money.tegro.market.db.Collection.new {
                            this.address = collection.address
                        }

                    dbCollection.owner = collection.owner
                    dbCollection.size = collection.size

                    liteClient.getNFTCollectionRoyalties(collection.address)?.let {
                        dbCollection.royaltyNumerator = it.first
                        dbCollection.royaltyDenominator = it.second
                        dbCollection.royaltyDestination = it.third
                    }
                }
            }
        }
    }
}

class AddItem(override val di: DI) :
    CliktCommand(name = "add-item", help = "Adds a singular collection to the database. Updates existing entries"),
    DIAware {
    val addresses by argument(
        name = "addresses",
        help = "NFT item contract address(es)"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val database: Database by instance()

            addresses.forEach { address ->
                val item = liteClient.getNFTItem(MsgAddressInt.AddrStd.parse(address))

                newSuspendedTransaction {
                    val dbItem =
                        Item.find {
                            (Items.workchain eq item.address.workchainId) and (Items.address eq item.address.address)
                        }.firstOrNull() ?: Item.new {
                            this.address = item.address
                            initialized = item is NFTItemInitialized
                        }

                    if (item is NFTItemInitialized) {
                        dbItem.initialized = true
                        dbItem.index = item.index

                        item.collection?.let { collection ->
                            val dbCollection = money.tegro.market.db.Collection.find {
                                (Collections.workchain eq collection.workchainId) and (Collections.address eq collection.address)
                            }.firstOrNull()

                            require(dbCollection != null) { "Collection ${collection.toString(userFriendly = true)} not in db" }

                            dbItem.collection = dbCollection
                        }

                        dbItem.owner = item.owner
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(AddCollection(di), AddItem(di)).main(args)
}
