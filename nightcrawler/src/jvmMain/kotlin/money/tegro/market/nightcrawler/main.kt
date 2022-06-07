package money.tegro.market.nightcrawler

import com.badoo.reaktive.coroutinesinterop.singleFromCoroutine
import com.badoo.reaktive.observable.*
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
import money.tegro.market.nft.*
import money.tegro.market.ton.ResilientLiteClient
import mu.KLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.conf.ConfigurableDI
import org.kodein.di.instance
import org.ton.api.tonnode.TonNodeBlockIdExt
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
            val collectionData = collectionAddresses.collections(liteClient)
            val collectionRoyalties = collectionAddresses.royalties(liteClient)
            val collectionMetadata = collectionData.metadata(ipfs)
            collectionData.upsert()
            collectionRoyalties.upsert()
            collectionMetadata.upsert()

            delay(4_000L);
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
            val itemData = itemAddresses.items(liteClient)
            itemData.upsert()
            itemAddresses.royalties(liteClient).upsert()
            itemData.metadata(ipfs, liteClient).upsert()

            delay(4_000L)
        }
    }

    companion object : KLogging()
}

class DBCollectionAddresses : Observable<MsgAddressIntStd> {
    override fun subscribe(observer: ObservableObserver<MsgAddressIntStd>) {

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
            val ipfs: IPFS by instance()

            // TODO
        }
    }

    companion object : KLogging()
}

fun Observable<MsgAddressIntStd>.collections(
    liteClient: LiteApi,
    referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { NFTCollection.of(it, liteClient, referenceBlock) } }

@JvmName("itemsNFTCollection")
fun Observable<NFTCollection>.items(
    liteClient: LiteApi,
    referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
) =
    this.flatMap {
        observable<Pair<MsgAddressIntStd, Long>> { emitter ->
            (0 until it.nextItemIndex).forEach { index ->
                emitter.onNext(it.address to index)
            }
        }
    }
        .flatMapSingle {
            singleFromCoroutine { NFTItem.of(it.first, it.second, liteClient, referenceBlock) }
        }

@JvmName("itemsMsgAddressIntStd")
fun Observable<MsgAddressIntStd>.items(
    liteClient: LiteApi,
    referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { NFTItem.of(it, liteClient, referenceBlock) } }

fun Observable<MsgAddressIntStd>.royalties(
    liteClient: LiteApi,
    referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
) =
    observable<Pair<MsgAddressIntStd, NFTRoyalty>> { emitter ->
        this.flatMapSingle { singleFromCoroutine { it to NFTRoyalty.of(it, liteClient, referenceBlock) } }
            .subscribe {
                val (address, royalty) = it
                if (royalty != null) emitter.onNext(address to royalty)
            }
    }

fun Observable<NFTCollection>.metadata(ipfs: IPFS) = this.flatMapSingle {
    singleFromCoroutine { it.address to NFTMetadata.of<NFTCollectionMetadata>(it.content, ipfs) }
}

fun Observable<NFTItem>.metadata(
    ipfs: IPFS, liteClient: LiteApi,
    referenceBlock: TonNodeBlockIdExt = runBlocking { liteClient.getMasterchainInfo().last },
) =
    observable<Pair<MsgAddressIntStd, NFTItemMetadata>> { emitter ->
        this.flatMapSingle {
            singleFromCoroutine {
                (it as? NFTItemInitialized)?.let {
                    it.address to NFTMetadata.of<NFTItemMetadata>(it.fullContent(liteClient, referenceBlock), ipfs)
                }
            }
        }.subscribe {
            if (it != null) emitter.onNext(it)
        }
    }

@JvmName("upsertNFTCollection")
fun Observable<NFTCollection>.upsert() =
    this.subscribe {
        transaction {
            val collection = CollectionEntity.find(it.address).firstOrNull() ?: CollectionEntity.new {
                discovered = Clock.System.now()
                workchain = it.address.workchainId
                address = it.address.address.toByteArray()
            }

            collection.run {
                ownerWorkchain = it.owner.workchainId
                ownerAddress = it.owner.address.toByteArray()
                nextItemIndex = it.nextItemIndex

                dataLastIndexed = Clock.System.now()
            }
        }
    }

@JvmName("upsertNFTItem")
fun Observable<NFTItem>.upsert() =
    this.subscribe {
        transaction {
            val item = ItemEntity.find(it.address).firstOrNull() ?: ItemEntity.new {
                discovered = Clock.System.now()
                workchain = it.address.workchainId
                address = it.address.address.toByteArray()
            }

            item.run {
                initialized = it is NFTItemInitialized

                if (it is NFTItemInitialized) {
                    index = it.index
                    this.collection = it.collection?.let { CollectionEntity.find(it).firstOrNull() }

                    ownerWorkchain = it.owner.workchainId
                    ownerAddress = it.owner.address.toByteArray()
                }

                dataLastIndexed = Clock.System.now()
            }
        }
    }

fun Observable<Pair<MsgAddressIntStd, NFTRoyalty>>.upsert() =
    this.subscribe {
        val (address, royalty) = it
        transaction {
            // If not in database, do nothing - since we don't know whether to add new item or collection in this case
            (ItemEntity.find(address).firstOrNull() ?: CollectionEntity.find(address).firstOrNull())?.run {
                royaltyNumerator = royalty.numerator
                royaltyDenominator = royalty.denominator
                royaltyDestinationWorkchain = address.workchainId
                royaltyDestinationAddress = address.address.toByteArray()

                royaltyLastIndexed = Clock.System.now()
            }
        }
    }

@JvmName("upsertMsgAddressIntStdNFTMetadata")
fun Observable<Pair<MsgAddressIntStd, NFTMetadata>>.upsert() =
    this.subscribe {
        val (address, metadata) = it
        transaction {
            // common metadata
            val entity = if (metadata is NFTItemMetadata) {
                ItemEntity.find(address).firstOrNull() ?: ItemEntity.new {
                    discovered = Clock.System.now()
                    workchain = address.workchainId
                    this.address = address.address.toByteArray()
                }
            } else {
                CollectionEntity.find(address).firstOrNull() ?: CollectionEntity.new {
                    discovered = Clock.System.now()
                    workchain = address.workchainId
                    this.address = address.address.toByteArray()
                }
            }

            entity.run {
                name = metadata.name
                description = metadata.description
                image = metadata.image
                imageData = metadata.imageData?.let { ExposedBlob(it) }

                metadataLastIndexed = Clock.System.now()
            }
        }

        if (metadata is NFTItemMetadata) {
            metadata.attributes.orEmpty().forEach { attribute ->
                transaction {
                    val item = ItemEntity.find(address).firstOrNull()
                    requireNotNull(item)
                    ItemAttributeEntity.find(item, attribute.trait).firstOrNull()?.run {
                        value = attribute.value
                    } ?: ItemAttributeEntity.new {
                        this.item = item
                        trait = attribute.trait
                        value = attribute.value
                    }
                }
            }
        } else if (metadata is NFTCollectionMetadata) {
            transaction {
                val collection = CollectionEntity.find(address).firstOrNull()
                requireNotNull(collection)
                collection.run {
                    coverImage = metadata.coverImage
                    coverImageData = metadata.coverImageData?.let { ExposedBlob(it) }
                }
            }
        }
    }

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(AddCollection(di), AddItem(di), IndexAll(di)).main(args)
}
