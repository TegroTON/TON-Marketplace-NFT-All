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
import money.tegro.market.db.CollectionEntity
import money.tegro.market.db.CollectionsTable
import money.tegro.market.db.ItemEntity
import money.tegro.market.db.ItemsTable
import money.tegro.market.nft.*
import mu.KLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
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
                bindSingleton {
                    Database.connect(databaseOptions.url, databaseOptions.driver, databaseConfig = DatabaseConfig {
                        useNestedTransactions = true
                    })
                }
            }

            val liteClient: LiteApi by instance()

            logger.debug("connecting to the lite client at ${liteServerOptions.host}:${liteServerOptions.port}")
            (liteClient as LiteClient).connect()

            val ipfs: IPFS by instance()

            logger.debug("initializing IPFS API")

            val db: Database by instance()

            logger.debug("connecting to the database ${db.vendor} v${db.version}")

            transaction {
                SchemaUtils.create(CollectionsTable, ItemsTable)
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
                val royalties = liteClient.getNFTCollectionRoyalties(collection.address)
                val metadata =

                    transaction {
                        CollectionEntity.find(collection.address).firstOrNull()?.let {
                            this.update(collection, royalties, metadata)
                        } ?: this.new(collection, royalties, metadata)
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
                    val dbItemEntity =
                        ItemEntity.find(item.address).firstOrNull() ?: ItemEntity.new {
                            this.address = item.address
                            initialized = item is NFTItemInitialized
                        }

                    if (item is NFTItemInitialized) {
                        dbItemEntity.initialized = true
                        dbItemEntity.index = item.index

                        item.collection?.let { collection ->
                            val dbCollectionEntity =
                                money.tegro.market.db.CollectionEntity.find(collection).firstOrNull()

                            require(dbCollectionEntity != null) { "Collection ${collection.toString(userFriendly = true)} not in db" }

                            dbItemEntity.collectionEntity = dbCollectionEntity
                        }

                        dbItemEntity.owner = item.owner
                    }
                }
            }
        }
    }
}

class IndexAll(override val di: DI) :
    CliktCommand(
        name = "index-all",
        help = "Updates information about all entries, collections and items, in the database"
    ),
    DIAware {
    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val database: Database by instance()

            newSuspendedTransaction {
                logger.debug("processing collections...")
                money.tegro.market.db.CollectionEntity.all().map { dbCollection ->
                    logger.debug("collection: ${dbCollection.address.toString(userFriendly = true)}")

                    val collection = liteClient.getNFTCollection(dbCollection.address)

                    dbCollection.owner = collection.owner
                    dbCollection.size = collection.size

                    liteClient.getNFTCollectionRoyalties(collection.address)?.let {
                        dbCollection.royaltyNumerator = it.first
                        dbCollection.royaltyDenominator = it.second
                        dbCollection.royaltyDestination = it.third
                    }
                }
            }

            logger.debug("processing collection items...")
            val collectionEntityItems = transaction {
                money.tegro.market.db.CollectionEntity.all().toList()
            }

            collectionEntityItems.forEach { dbCollection ->
                logger.debug("collection: ${dbCollection.address.toString(userFriendly = true)}")

                for (i in 0 until dbCollection.size) {
                    val itemAddress = liteClient.getNFTCollectionItem(dbCollection.address, i)
                    logger.debug("item no. $i: ${itemAddress.toString(userFriendly = true)}")

                    val item = liteClient.getNFTItem(itemAddress)

                    transaction {
                        val dbItemEntity =
                            ItemEntity.find(item.address).firstOrNull() ?: ItemEntity.new {
                                this.address = item.address
                                initialized = item is NFTItemInitialized
                            }

                        // Even for uninitialized items, we can easily deduce collection and their index for the future
                        dbItemEntity.collectionEntity = dbCollection
                        dbItemEntity.index = i

                        if (item is NFTItemInitialized) {
                            dbItemEntity.owner = item.owner
                        }
                    }
                }
            }

            // TODO: process items not belonging to collections
        }
    }

    companion object : KLogging()
}

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(AddCollection(di), AddItem(di), IndexAll(di)).main(args)
}

fun CollectionEntity.Companion.new(
    collection: NFTCollection,
    royalties: Triple<Int, Int, MsgAddressInt.AddrStd>?,
    metadata: NFTCollectionMetadata
): CollectionEntity =
    this.new {
        this.update(collection, royalties, metadata)
    }

fun CollectionEntity.update(
    collection: NFTCollection,
    royalties: Triple<Int, Int, MsgAddressInt.AddrStd>?,
    metadata: NFTCollectionMetadata
) {
    address = collection.address
    owner = collection.owner
    nextItemIndex = collection.size

    royalties?.let {
        royaltyNumerator = it.first
        royaltyDenominator = it.second
        royaltyDestination = it.third
    }

    if (metadata is NFTCollectionMetadataOffChainHttp) {
        metadataUrl = metadata.url
        metadataIpfs = null
    } else if (metadata is NFTCollectionMetadataOffChainIpfs) {
        metadataUrl = null
        metadataIpfs = metadata.id
    }

    name = metadata.name
    description = metadata.description

    if (metadata.image is NFTContentOffChainHttp) {
        imageUrl = (metadata.image as NFTContentOffChainHttp).url
        imageIpfs = null
        imageData = null
    } else if (metadata.image is NFTContentOffChainIpfs) {
        imageUrl = null
        imageIpfs = (metadata.image as NFTContentOffChainIpfs).id
        imageData = null
    } else if (metadata.image is NFTContentOnChain) {
        imageUrl = null
        imageIpfs = null
        imageData = ExposedBlob((metadata.image as NFTContentOnChain).data)
    }

    if (metadata.coverImage is NFTContentOffChainHttp) {
        coverImageUrl = (metadata.coverImage as NFTContentOffChainHttp).url
        coverImageIpfs = null
        coverImageData = null
    } else if (metadata.coverImage is NFTContentOffChainIpfs) {
        coverImageUrl = null
        coverImageIpfs = (metadata.coverImage as NFTContentOffChainIpfs).id
        coverImageData = null
    } else if (metadata.coverImage is NFTContentOnChain) {
        coverImageUrl = null
        coverImageIpfs = null
        coverImageData = ExposedBlob((metadata.coverImage as NFTContentOnChain).data)
    }
}
