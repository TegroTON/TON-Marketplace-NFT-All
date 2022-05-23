package money.tegro.market.nightcrawler

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.ipfs.api.IPFS
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTCollection
import mu.KLogging
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.ton.block.MsgAddressInt
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi

object ItemsTable : Table() {
    val workchain = integer("workchain")
    val address = varchar("address", 64).uniqueIndex()
    val initialized = bool("initialized")
    val index = integer("index")

    // TODO: collection, content
    override val primaryKey = PrimaryKey(workchain, address)
}

class LiteClientOptions : OptionGroup("lite-client options") {
    val host by option("-o", "--host", help = "Lite server host IP address")
        .default("5.9.10.47")
    val port by option("-p", "--port", help = "Lite server port number").int().default(19949)
    val publicKey by option("-k", "--pubkey", help = "Lite server public key")
        .default("9f85439d2094b92a639c2c9493d7b740e39dea8d08b525986d39d6dd69e7f309")
}

class DBOptions : OptionGroup("database options") {
    val url by option("--db-url", help = "Database url")
        .default("jdbc:sqlite:../build/nighcrawler.db")
    val driver by option("--db-driver", help = "Database driver")
        .default("org.sqlite.JDBC")
}

class Tool : CliktCommand(name = "nighcrawler", help = ""), KoinComponent {
    private val liteClientOptions by LiteClientOptions()
    private val dbOptions by DBOptions()
    private val ipfsAddress by option("-i", "--ipfs", help = "Address of the IPFS API server")
        .default("/ip4/127.0.0.1/tcp/5001")
    private val verbose by option("-v", "--verbose", help = "Verbose output").int().default(0)

    override fun run() {
        runBlocking {
            when (verbose) {
                0 -> {
                    getKoin().logger.level = Level.ERROR
                    (KotlinLogging.logger(org.slf4j.Logger.ROOT_LOGGER_NAME).underlyingLogger as ch.qos.logback.classic.Logger).level =
                        ch.qos.logback.classic.Level.WARN
                }
                1 -> {
                    getKoin().logger.level = Level.INFO
                    (KotlinLogging.logger(org.slf4j.Logger.ROOT_LOGGER_NAME).underlyingLogger as ch.qos.logback.classic.Logger).level =
                        ch.qos.logback.classic.Level.INFO
                }
                2 -> {
                    getKoin().logger.level = Level.DEBUG
                    (KotlinLogging.logger(org.slf4j.Logger.ROOT_LOGGER_NAME).underlyingLogger as ch.qos.logback.classic.Logger).level =
                        ch.qos.logback.classic.Level.DEBUG
                }
                else ->
                    logger.warn("Verbose level $verbose is not valid, ignoring")
            }

            val liteClient: LiteApi by inject {
                parametersOf(liteClientOptions.host, liteClientOptions.port, hex(liteClientOptions.publicKey), 0)
            }

            logger.debug("connecting to the lite client at ${liteClientOptions.host}:${liteClientOptions.port}")
            (liteClient as ResilientLiteClient).connect()

            val ipfs: IPFS by inject {
                parametersOf(ipfsAddress)
            }

            logger.debug("ipfs api ${ipfs.version()} is initialized")

            val db: Database by inject {
                parametersOf(dbOptions.url, dbOptions.driver)
            }
            logger.debug("database ${db.vendor} at ${db.version} connection is set up")

            transaction(db) {
                addLogger(Slf4jSqlDebugLogger)
                SchemaUtils.create(ItemsTable)

                commit()
            }
        }
    }

    companion object : KLogging()
}

class Index :
    CliktCommand(name = "index", help = "Index an NFT collection and all of its items"),
    KoinComponent {
    val address by argument(name = "address", help = "NFT collection contract address")
    override fun run() {
        runBlocking {
            val collection = NFTCollection.fetch(MsgAddressInt.AddrStd.parse(address))

            for (i in 1..collection.size) { // TODO: THIS SKIPS FIRST ELEMENT
                val item = collection.getItem(i)

                val db: Database by inject()
                transaction {
                    addLogger(Slf4jSqlDebugLogger)

                    ItemsTable.insertIgnore {
                        it[workchain] = (item.address as MsgAddressInt.AddrStd).workchain_id
                        it[address] = hex((item.address as MsgAddressInt.AddrStd).address)
                        it[initialized] = item.initialized
                        it[index] = item.index
                    }
                    commit()
                }
            }
        }
    }
}

class KoinLogger : Logger(Level.NONE) {
    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.debug(msg)
            Level.INFO -> logger.info(msg)
            Level.ERROR -> logger.error(msg)
            Level.NONE -> {}
        }
    }

    companion object : KLogging()
}

suspend fun main(args: Array<String>) {
    startKoin {
        logger(KoinLogger())

        modules(module {
            single { params ->
                ResilientLiteClient(params.get(), params.get(), params.get(), params.get()) as LiteApi
            }
            single { (addr: String) ->
                IPFS(addr)
            }
            single { (url: String, driver: String) ->
                Database.connect(url, driver)
            }
        })
    }

    Tool().subcommands(Index()).main(args)
}
