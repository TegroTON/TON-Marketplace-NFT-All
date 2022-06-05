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
import money.tegro.market.db.*
import money.tegro.market.nft.*
import money.tegro.market.ton.ResilientLiteClient
import mu.KLogging
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.RichSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.util.Collector
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.conf.ConfigurableDI
import org.kodein.di.conf.DIGlobalAware
import org.kodein.di.conf.global
import org.kodein.di.instance
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressIntStd
import org.ton.block.VmStackValue
import org.ton.boc.BagOfCells
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb


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
    private val addresses by argument(
        name = "addresses",
        help = "NFT item contract address(es)"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()
            val ipfs: IPFS by instance()

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

                metadata?.attributes?.forEach { attribute ->
                    transaction {
                        val dbItem = ItemEntity.find(item.address).firstOrNull()
                        requireNotNull(dbItem)
                        ItemAttributeEntity.find(dbItem, attribute.trait).firstOrNull()?.run {
                            value = attribute.value
                        } ?: ItemAttributeEntity.new {
                            this.item = dbItem
                            trait = attribute.trait
                            value = attribute.value
                        }
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
        val liteClient: LiteApi by instance()

        val env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(Configuration())

        val dbCollectionAddresses = env.addSource(CollectionsTableAddrSource())

        val dbCollectionData = dbCollectionAddresses.process(Addr2Coll())

        val collectionItems = dbCollectionData.process(Coll2ItemAddr()).process(Addr2Item())

        collectionItems.print()

        collectionItems.addSink(ItemsTableSink())

        env.execute()
    }

    companion object : KLogging()
}

fun main(args: Array<String>) {
    val di = DI.global
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

    when (metadata.image) {
        is NFTContentOffChainHttp -> {
            imageUrl = (metadata.image as NFTContentOffChainHttp).url
            imageIpfs = null
            imageData = null
        }
        is NFTContentOffChainIpfs -> {
            imageUrl = null
            imageIpfs = (metadata.image as NFTContentOffChainIpfs).id
            imageData = null
        }
        is NFTContentOnChain -> {
            imageUrl = null
            imageIpfs = null
            imageData = ExposedBlob((metadata.image as NFTContentOnChain).data)
        }
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
        this.collection = item.collection?.let { money.tegro.market.db.CollectionEntity.find(it).firstOrNull() }
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

        when (metadata.image) {
            is NFTContentOffChainHttp -> {
                imageUrl = (metadata.image as NFTContentOffChainHttp).url
                imageIpfs = null
                imageData = null
            }
            is NFTContentOffChainIpfs -> {
                imageUrl = null
                imageIpfs = (metadata.image as NFTContentOffChainIpfs).id
                imageData = null
            }
            is NFTContentOnChain -> {
                imageUrl = null
                imageIpfs = null
                imageData = ExposedBlob((metadata.image as NFTContentOnChain).data)
            }
        }
    }
}

data class Addr(
    var workchain: Int,
    var address: ByteArray,
)

data class Coll(
    var address: Addr,
    var nextItemIndex: Long,
    var content: ByteArray,
    var owner: Addr,
)

data class Item(
    var address: Addr,
    var initialized: Boolean,
    var index: Long?,
    var collection: Addr?,
    var owner: Addr?,
    var content: ByteArray?,
)

data class Royal(
    var numerator: Int,
    var denominator: Int,
    var destination: Addr,
)

data class Attrib(
    var trait: String,
    var value: String
)

abstract class Cont

data class ContOff(
    var url: String
) : Cont()

data class ContOn(
    var data: ByteArray
) : Cont()

data class Meta(
    var name: String?,
    var description: String?,
    var image: Cont?,
    var coverImage: Cont?,
    val attributes: List<NFTItemAttribute>?
)

class Addr2Coll : ProcessFunction<Addr, Coll>(), DIGlobalAware {
    companion object : KLogging()

    override fun processElement(value: Addr?, ctx: Context?, out: Collector<Coll>?) {
        val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        value?.let {
            runBlocking {
                val liteClient: LiteApi by instance()
                val lastBlock = liteClient.getMasterchainInfo().last

                logger.debug { "running method `get_collection_data` on ${it.workchain}:${hex(it.address)}" }
                val result = liteClient.runSmcMethod(
                    0b100,
                    lastBlock,
                    LiteServerAccountId(it.workchain, it.address),
                    "get_collection_data"
                )
                logger.debug { "result: $result" }
                require(result.exitCode == 0) { "failed to run the method, exit code is ${result.exitCode}" }

                val nextItemIndex = (result[0] as VmStackValue.TinyInt).value
                val content = BagOfCells((result[1] as VmStackValue.Cell).cell).toByteArray()
                val owner = (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as MsgAddressIntStd
                out?.collect(Coll(it, nextItemIndex, content, Addr(owner.workchainId, owner.address.toByteArray())))
            }
        }
    }
}

class Addr2Item : ProcessFunction<Addr, Item>(), DIGlobalAware {
    companion object : KLogging()

    override fun processElement(value: Addr?, ctx: Context?, out: Collector<Item>?) {
        val msgAddressCodec by lazy { MsgAddress.tlbCodec() }
        value?.let {
            runBlocking {
                val liteClient: LiteApi by instance()
                val lastBlock = liteClient.getMasterchainInfo().last

                logger.debug { "running method `get_nft_data` on ${it.workchain}:${hex(it.address)}" }
                val result = liteClient.runSmcMethod(
                    0b100,
                    lastBlock,
                    LiteServerAccountId(it.workchain, it.address),
                    "get_nft_data"
                )
                logger.debug { "result: $result" }
                if (result.exitCode != 0) {
                    logger.warn { "Method exit code was ${result.exitCode}. NFT is most likely not initialized" }
                    out?.collect(Item(it, false, null, null, null, null))
                    return@runBlocking
                }

                if ((result[0] as VmStackValue.TinyInt).value == -1L) {
                    val index = (result[1] as VmStackValue.TinyInt).value
                    val collection =
                        (result[2] as? VmStackValue.Slice)?.toCellSlice()
                            ?.loadTlb(msgAddressCodec) as? MsgAddressIntStd
                    val owner =
                        (result[3] as VmStackValue.Slice).toCellSlice()
                            .loadTlb(msgAddressCodec) as MsgAddressIntStd
                    val content = BagOfCells((result[4] as VmStackValue.Cell).cell).toByteArray()
                    out?.collect(
                        Item(
                            it,
                            true,
                            index,
                            collection?.let { Addr(it.workchainId, it.address.toByteArray()) },
                            Addr(owner.workchainId, owner.address.toByteArray()),
                            content
                        )
                    )
                } else {
                    out?.collect(Item(it, false, null, null, null, null))
                }
            }
        }
    }
}

class Coll2ItemAddr : ProcessFunction<Coll, Addr>(), DIGlobalAware {
    companion object : KLogging()

    override fun processElement(value: Coll?, ctx: Context?, out: Collector<Addr>?) {
        val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        value?.let {
            runBlocking {
                val liteClient: LiteApi by instance()
                val lastBlock = liteClient.getMasterchainInfo().last

                logger.debug { "indexing all ${it.nextItemIndex} items of the ${it.address.workchain}:${hex(it.address.address)} collection" }
                for (i in 0 until it.nextItemIndex) {
                    logger.debug { "running method `get_nft_address_by_index` on ${it.address.workchain}:${hex(it.address.address)} with index $i" }
                    val result = liteClient.runSmcMethod(
                        0b100,
                        lastBlock,
                        LiteServerAccountId(it.address.workchain, it.address.address),
                        "get_nft_address_by_index",
                        VmStackValue.TinyInt(i)
                    )

                    require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

                    val item = (result.first() as VmStackValue.Slice).toCellSlice()
                        .loadTlb(msgAddressCodec) as MsgAddressIntStd

                    out?.collect(Addr(item.workchainId, item.address.toByteArray()))
                }
                logger.debug { "finished processing items of ${it.address.workchain}:${hex(it.address.address)} collection" }
            }
        }
    }
}

class CollectionsTableAddrSource : RichSourceFunction<Addr>() {
    private var isCancelled = false
    override fun run(ctx: SourceFunction.SourceContext<Addr>?) {
        transaction {
            CollectionEntity.all()
                .takeUnless { isCancelled }
                ?.map { Addr(it.rawWorkchain, it.rawAddress) }
                ?.forEach {
                    ctx?.collect(it)
                }
        }
    }

    override fun cancel() {
        isCancelled = true
    }
}

class ItemsTableSink : RichSinkFunction<Item>() {
    companion object : KLogging()

    private fun ItemEntity.update(it: Item) {
        lastIndexed = Clock.System.now()
        initialized = it.initialized
        index = it.index
        it.collection?.let {
            CollectionEntity.find((CollectionsTable.workchain eq it.workchain) and (CollectionsTable.address eq it.address))
                .firstOrNull()?.let {
                    collection = it
                }
        }
        it.owner?.let {
            rawOwnerWorkchain = it.workchain
            rawOwnerAddress = it.address
        }
        // TODO: content
    }

    override fun invoke(value: Item?, context: SinkFunction.Context?) {
        value?.let {
            transaction {
                ItemEntity.find((ItemsTable.workchain eq it.address.workchain) and (ItemsTable.address eq it.address.address))
                    .firstOrNull()?.run {
                        logger.debug { "updating item ${it.address.workchain}:${hex(it.address.address)}" }
                        this.update(it)
                    } ?: ItemEntity.new {
                    logger.debug { "new item  ${it.address.workchain}:${hex(it.address.address)}" }
                    discovered = Clock.System.now()
                    rawWorkchain = it.address.workchain
                    rawAddress = it.address.address
                    this.update(it)
                }
            }
        }
    }
}


//
//class Addr2Royal : ProcessFunction<Addr, Royal>
//
//class Item2Meta : ProcessFunction<Item, Meta>
//
//class Coll2Meta : ProcessFunction<Item, Meta>

