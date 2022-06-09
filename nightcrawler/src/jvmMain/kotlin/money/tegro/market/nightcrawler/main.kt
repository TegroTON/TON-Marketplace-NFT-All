package money.tegro.market.nightcrawler

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.singleScheduler
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import money.tegro.market.db.*
import money.tegro.market.nft.NFTItemInitialized
import money.tegro.market.ton.ResilientLiteClient
import mu.KLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
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
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime


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
        .default("jdbc:sqlite:../build/nightcrawler.db")
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
                        base64(liteServerOptions.publicKey)
                    )
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

            val db: Database by instance()

            logger.debug("connecting to the database ${db.vendor} v${db.version}")

            transaction {
                SchemaUtils.create(CollectionsTable, ItemsTable, ItemAttributesTable)
            }
        }
    }

    companion object : KLogging()
}

class AddCollection(override val di: DI) :
    CliktCommand(name = "add-collection", help = "Adds a collection to the database. Updates existing entries"),
    DIAware {
    private val addresses by argument(
        name = "addresses",
        help = "NFT collection contract address(es)"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val ipfs: IPFS by instance()

            val collectionAddresses = addresses.asObservable().map { MsgAddressIntStd.parse(it) }
            val collectionData = collectionAddresses.nftCollectionOf(liteClient)
            val collectionRoyalties = collectionAddresses.nftRoyaltyOf(liteClient)
            val collectionMetadata = collectionData.nftCollectionMetadata(ipfs)

            collectionData.upsertCollectionData()
            collectionRoyalties.updateRoyalty()
            collectionMetadata.upsertCollectionMetadata()

            concat(
                collectionAddresses,
                collectionData,
                collectionRoyalties,
                collectionMetadata
            ).subscribe(
                onComplete = {
                    runBlocking { delay(5_000L) }
                    logger.debug { "completed" }
                    exitProcess(0)
                }
            )
            delay(Long.MAX_VALUE)
        }
    }

    companion object : KLogging()
}

class AddItem(override val di: DI) :
    CliktCommand(name = "add-item", help = "Adds a singular collection to the database. Updates existing entries"),
    DIAware {
    private val addresses by argument(
        name = "addresses",
        help = "NFT item contract address(es)"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val ipfs: IPFS by instance()

            val itemAddresses = addresses.asObservable().map { MsgAddressIntStd.parse(it) }
            val itemData = itemAddresses.nftItemOf(liteClient)
            val itemRoyalties = itemAddresses.nftRoyaltyOf(liteClient)
            val itemSellers = itemData.filter { it is NFTItemInitialized }.map { (it as NFTItemInitialized).owner }
                .nftSaleOf(liteClient)
            val itemMetadata = itemData.nftItemMetadata(ipfs, liteClient)

            itemData.upsertItemData()
            itemRoyalties.updateRoyalty()
            itemSellers.upsertItemSale()
            itemMetadata.upsertItemMetadata()

            concat(
                itemAddresses,
                itemData,
                itemRoyalties,
                itemSellers,
                itemMetadata
            ).subscribe(
                onComplete = {
                    runBlocking { delay(5_000L) }
                    logger.debug { "completed" }
                    exitProcess(0)
                }
            )
            delay(Long.MAX_VALUE)
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

            val collectionAddresses = databaseCollections().subscribeOn(singleScheduler)

            val collectionData = collectionAddresses.observeOn(ioScheduler).nftCollectionOf(liteClient)
            val collectionRoyalties = collectionAddresses.observeOn(ioScheduler).nftRoyaltyOf(liteClient)
            val collectionMetadata = collectionData.observeOn(ioScheduler).nftCollectionMetadata(ipfs)

            val itemAddresses = concat(
                collectionData.observeOn(ioScheduler).nftCollectionItems(liteClient),
                // Include all of the items without collections
                databaseItems({ it.collection == null }).subscribeOn(singleScheduler),
            )
            val itemData = itemAddresses.observeOn(ioScheduler).nftItemOf(liteClient)
            val itemRoyalties = itemAddresses.observeOn(ioScheduler).nftRoyaltyOf(liteClient)
            val itemSellers = itemData.observeOn(ioScheduler).filter { it is NFTItemInitialized }
                .map { (it as NFTItemInitialized).owner }
                .nftSaleOf(liteClient)
            val itemMetadata = itemData.observeOn(ioScheduler).nftItemMetadata(ipfs, liteClient)

            collectionData.observeOn(singleScheduler).upsertCollectionData()
            collectionRoyalties.observeOn(singleScheduler).updateRoyalty()
            collectionMetadata.observeOn(singleScheduler).upsertCollectionMetadata()

            itemData.observeOn(singleScheduler).upsertItemData()
            itemRoyalties.observeOn(singleScheduler).updateRoyalty()
            itemSellers.observeOn(singleScheduler).upsertItemSale()
            itemMetadata.observeOn(singleScheduler).upsertItemMetadata()

            concat(
                collectionAddresses,
                collectionData,
                collectionRoyalties,
                collectionMetadata,
                itemAddresses,
                itemData,
                itemRoyalties,
                itemSellers,
                itemMetadata
            ).subscribe(
                onComplete = {
                    runBlocking { delay(5_000L) }
                    logger.debug { "completed" }
                    exitProcess(0)
                }
            )
            delay(Long.MAX_VALUE)
        }
    }

    companion object : KLogging()
}

@OptIn(ExperimentalTime::class)
class Steady(override val di: DI) :
    CliktCommand(
        name = "steady",
        help = "Main service, keeps entire database updated"
    ),
    DIAware {
    private val collectionDataUpdatePeriod by option(
        "--collection-data-period",
        help = "Time delay (in minutes), after which collection data will be queued for an update"
    )
        .int()
        .default(69)
    private val collectionRoyaltyUpdatePeriod by option(
        "--collection-royalty-period",
        help = "Time delay (in minutes), after which collection royalty data will be queued for an update"
    )
        .int()
        .default(420)
    private val collectionMetadataUpdatePeriod by option(
        "--collection-metadata-period",
        help = "Time delay (in minutes), after which collection metadata will be queued for an update"
    )
        .int()
        .default(420)


    private val itemDataUpdatePeriod by option(
        "--item-data-period",
        help = "Time delay (in minutes), after which item data will be queued for an update"
    )
        .int()
        .default(6)
    private val itemOrphanRoyaltyUpdatePeriod by option(
        "--item-orphan-royalty-period",
        help = "Time delay (in minutes), after which item royalty data for orphan NFTs (not belonging to a collection) will be queued for an update"
    )
        .int()
        .default(420)
    private val itemRoyaltyUpdatePeriod by option(
        "--item-royalty-period",
        help = "Time delay (in minutes), after which item royalty data will be queued for an update"
    )
        .int()
        .default(420 * 6)
    private val itemOwnerUpdatePeriod by option(
        "--item-owner-period",
        help = "Time delay (in minutes), after which item's owner data will be queued for an update"
    )
        .int()
        .default(9)
    private val itemMetadataUpdatePeriod by option(
        "--item-metadata-period",
        help = "Time delay (in minutes), after which item metadata will be queued for an update"
    )
        .int()
        .default(420)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val ipfs: IPFS by instance()

            val collectionAddresses =
                observableInterval(100L, singleScheduler)
                    .flatMap(1) {
                        databaseCollections().subscribeOn(singleScheduler)
                    }

            val collectionData = collectionAddresses.observeOn(singleScheduler)
                .filter {
                    transaction {
                        CollectionEntity.find(it).firstOrNull()?.dataLastIndexed?.let {
                            it > Clock.System.now() + collectionDataUpdatePeriod.minutes
                        } ?: true
                    }
                }
                .observeOn(ioScheduler)
                .nftCollectionOf(liteClient)

            val collectionRoyalties = collectionAddresses.observeOn(singleScheduler)
                .filter {
                    transaction {
                        CollectionEntity.find(it).firstOrNull()?.royaltyLastIndexed?.let {
                            it > Clock.System.now() + collectionRoyaltyUpdatePeriod.minutes
                        } ?: true
                    }
                }
                .observeOn(ioScheduler)
                .nftRoyaltyOf(liteClient)

            val collectionMetadata = collectionData.observeOn(singleScheduler)
                .filter {
                    transaction {
                        CollectionEntity.find(it.address).firstOrNull()?.metadataLastIndexed?.let {
                            it > Clock.System.now() + collectionMetadataUpdatePeriod.minutes
                        } ?: true
                    }
                }.observeOn(ioScheduler)
                .nftCollectionMetadata(ipfs)

            val itemAddresses = merge(
                collectionData.observeOn(ioScheduler).nftCollectionItems(liteClient),
                // Include all of the items without collections
                observableInterval(100L, singleScheduler)
                    .flatMap(1) {
                        databaseItems({ it.collection == null }).subscribeOn(singleScheduler)
                    }
            )
            val itemData = itemAddresses.observeOn(singleScheduler)
                .filter {
                    transaction {
                        ItemEntity.find(it).firstOrNull()?.dataLastIndexed?.let {
                            it > Clock.System.now() + itemDataUpdatePeriod.minutes
                        } ?: true
                    }
                }.observeOn(ioScheduler).nftItemOf(liteClient)
            val itemRoyalties = itemAddresses.observeOn(singleScheduler)
                .filter {
                    transaction {
                        ItemEntity.find(it).firstOrNull()?.run {
                            if (collection == null) { // We've got an orphan
                                royaltyLastIndexed?.let { it > Clock.System.now() + itemOrphanRoyaltyUpdatePeriod.minutes }
                            } else {
                                royaltyLastIndexed?.let { it > Clock.System.now() + itemRoyaltyUpdatePeriod.minutes }
                            }
                        } ?: true
                    }
                }.observeOn(ioScheduler).nftRoyaltyOf(liteClient)
            val itemSellers = itemData.observeOn(ioScheduler).filter { it is NFTItemInitialized }
                .map { (it as NFTItemInitialized).owner }
                .observeOn(singleScheduler)
                .filter {
                    transaction {
                        ItemEntity.find(it).firstOrNull()?.ownerLastIndexed?.let {
                            it > Clock.System.now() + itemOwnerUpdatePeriod.minutes
                        } ?: true
                    }
                }
                .observeOn(ioScheduler)
                .nftSaleOf(liteClient)
            val itemMetadata = itemData.observeOn(singleScheduler)
                .filter {
                    transaction {
                        ItemEntity.find(it.address).firstOrNull()?.dataLastIndexed?.let {
                            it > Clock.System.now() + itemMetadataUpdatePeriod.minutes
                        } ?: true
                    }
                }.observeOn(ioScheduler)
                .nftItemMetadata(ipfs, liteClient)

            collectionData.observeOn(singleScheduler).upsertCollectionData()
            collectionRoyalties.observeOn(singleScheduler).updateRoyalty()
            collectionMetadata.observeOn(singleScheduler).upsertCollectionMetadata()

            itemData.observeOn(singleScheduler).upsertItemData()
            itemRoyalties.observeOn(singleScheduler).updateRoyalty()
            itemSellers.observeOn(singleScheduler).upsertItemSale()
            itemMetadata.observeOn(singleScheduler).upsertItemMetadata()

            delay(Long.MAX_VALUE)
        }
    }

    companion object : KLogging()
}

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(AddCollection(di), AddItem(di), IndexAll(di), Steady(di)).main(args)
}
