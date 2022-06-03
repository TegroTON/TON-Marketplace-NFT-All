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
import kotlinx.datetime.Clock
import money.tegro.market.db.CollectionEntity
import money.tegro.market.db.CollectionsTable
import money.tegro.market.db.ItemEntity
import money.tegro.market.db.ItemsTable
import money.tegro.market.nft.*
import money.tegro.market.ton.ResilientLiteClient
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
                    ResilientLiteClient(
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
            val ipfs: IPFS by instance()
            val database: Database by instance()

            addresses.forEach { address ->
                val collection = liteClient.getNFTCollection(MsgAddressIntStd.parse(address))
                val royalties = liteClient.getNFTCollectionRoyalties(collection.address)
                val metadata = NFTCollectionMetadata.of(collection.content, ipfs)

                transaction {
                    CollectionEntity.find(collection.address).firstOrNull()?.run {
                        logger.debug { "updating already existing collection ${this.address.toString(userFriendly = true)}" }
                        update(collection, royalties, metadata)
                    } ?: CollectionEntity.new(collection, royalties, metadata).run {
                        logger.debug { "new collection ${this.address.toString(userFriendly = true)}" }
                    }
                }
            }
        }
    }

    companion object : KLogging()
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
            val ipfs: IPFS by instance()
            val database: Database by instance()

            addresses.forEach { address ->
                val item = liteClient.getNFTItem(MsgAddressIntStd.parse(address))
                val royalties = liteClient.getNFTItemRoyalties(item.address)
                val metadata =
                    if (item is NFTItemInitialized) NFTItemMetadata.of(item.fullContent(liteClient), ipfs) else null

                transaction {
                    ItemEntity.find(item.address).firstOrNull()?.run {
                        logger.debug { "updating already existing item ${this.address.toString(userFriendly = true)}" }
                        update(item, royalties, metadata)
                    } ?: ItemEntity.new(item, royalties, metadata).run {
                        logger.debug { "new item ${this.address.toString(userFriendly = true)}" }
                    }
                }
            }
        }
    }

    companion object : KLogging()
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
            val ipfs: IPFS by instance()
            val database: Database by instance()

            newSuspendedTransaction {
                logger.debug { "processing collections..." }
                CollectionEntity.all().map { dbCollection ->
                    logger.debug { "updating collection ${dbCollection.address.toString(userFriendly = true)}" }

                    val collection = liteClient.getNFTCollection(dbCollection.address)
                    val royalties = liteClient.getNFTCollectionRoyalties(dbCollection.address)
                    val metadata = NFTCollectionMetadata.of(collection.content, ipfs)

                    transaction {
                        dbCollection.update(collection, royalties, metadata)
                    }
                }
            }

            logger.debug { "processing collection items..." }
            val allCollections = transaction {
                CollectionEntity.all().toList()
            }

            allCollections.forEach { dbCollection ->
                logger.debug {
                    "processing all ${dbCollection.nextItemIndex} items of collection ${
                        dbCollection.address.toString(
                            userFriendly = true
                        )
                    }"
                }

                for (i in 0 until dbCollection.nextItemIndex) {
                    val itemAddress = liteClient.getNFTCollectionItem(dbCollection.address, i)
                    logger.debug { "item no. $i: ${itemAddress.toString(userFriendly = true)}" }

                    val item = liteClient.getNFTItem(itemAddress)
                    val royalties = liteClient.getNFTItemRoyalties(item.address)
                    val metadata =
                        if (item is NFTItemInitialized) NFTItemMetadata.of(item.fullContent(liteClient), ipfs) else null

                    transaction {
                        ItemEntity.find(item.address).firstOrNull()?.run {
                            logger.debug { "updating already existing item ${this.address.toString(userFriendly = true)}" }
                            update(item, royalties, metadata)
                        } ?: ItemEntity.new(item, royalties, metadata).run {
                            logger.debug { "new item ${this.address.toString(userFriendly = true)}" }
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
    royalties: Triple<Int, Int, MsgAddressIntStd>?,
    metadata: NFTCollectionMetadata
): CollectionEntity =
    this.new {
        discovered = Clock.System.now()
        update(collection, royalties, metadata)
    }

fun CollectionEntity.update(
    collection: NFTCollection,
    royalties: Triple<Int, Int, MsgAddressIntStd>?,
    metadata: NFTCollectionMetadata
) {
    lastIndexed = Clock.System.now()
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


fun ItemEntity.Companion.new(
    item: NFTItem,
    royalties: Triple<Int, Int, MsgAddressIntStd>?,
    metadata: NFTItemMetadata?
): ItemEntity =
    this.new {
        discovered = Clock.System.now()
        update(item, royalties, metadata)
    }

fun ItemEntity.update(
    item: NFTItem,
    royalties: Triple<Int, Int, MsgAddressIntStd>?,
    metadata: NFTItemMetadata?
) {
    lastIndexed = Clock.System.now()
    address = item.address
    initialized = item is NFTItemInitialized

    if (item is NFTItemInitialized) {
        this.collection = item.collection?.let { CollectionEntity.find(it).firstOrNull() }
        owner = item.owner
    }

    royalties?.let {
        royaltyNumerator = it.first
        royaltyDenominator = it.second
        royaltyDestination = it.third
    }

    metadata?.let { metadata ->
        if (metadata is NFTItemMetadataOffChainHttp) {
            metadataUrl = metadata.url
            metadataIpfs = null
        } else if (metadata is NFTItemMetadataOffChainIpfs) {
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
    }
}
