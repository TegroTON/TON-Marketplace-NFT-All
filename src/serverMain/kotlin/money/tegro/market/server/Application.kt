package money.tegro.market.server

import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import io.github.reactivecircus.cache4k.Cache
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.server.controller.*
import money.tegro.market.server.logging.TonLogger
import money.tegro.market.server.properties.MarketplaceProperties
import money.tegro.market.server.repository.ApprovalRepository
import money.tegro.market.server.repository.CollectionRepository
import money.tegro.market.server.repository.ItemRepository
import money.tegro.market.server.repository.RoyaltyRepository
import money.tegro.market.server.service.EvictionService
import money.tegro.market.server.service.LiveBlockService
import money.tegro.market.server.service.ReferenceBlockService
import money.tegro.market.server.table.ApprovalTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller
import org.kodein.di.ktor.di
import org.slf4j.event.Level
import org.ton.adnl.client.engine.cio.CIOAdnlClientEngine
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*
import kotlin.time.Duration.Companion.hours

fun main(args: Array<String>): Unit = EngineMain.main(args)

@OptIn(ExperimentalSerializationApi::class)
fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = humanReadableSerializerModule
            }
        )
    }
    install(Resources)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respondRedirect("/#404")
        }
    }

    di {
        bindSingleton { MarketplaceProperties.fromEnvironment(environment) }

        bindSingleton { TonLogger() }
        bindSingleton { CIOAdnlClientEngine.create() }
        bindSingleton {
            val config =
                environment.config.propertyOrNull("lite-api.config")?.getString() ?: "testnet-global.config.json"

            LiteClient(
                instance(),
                Json {
                    ignoreUnknownKeys = true
                }
                    .decodeFromStream(requireNotNull(ClassLoader.getSystemResourceAsStream(config)) { "Could not load Lite Api config." }),
                instance()
            )
        }
        bindSingleton {
            HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 10_000L
                }
                install(HttpRequestRetry) {
                    maxRetries = 5
                    retryOnServerErrors()
                    retryOnException()
                    exponentialDelay()
                }
            }
        }

        bindEagerSingleton { ReferenceBlockService(di) }
        bindEagerSingleton { LiveBlockService(di) }
        bindEagerSingleton { EvictionService(di) }

        bindSingleton {
            Database.connect(
                url = environment.config.propertyOrNull("database.url")?.getString()
                    ?: "jdbc:postgresql://localhost:5432/market",
                driver = environment.config.propertyOrNull("database.driver")?.getString()
                    ?: "org.postgresql.Driver",
                user = environment.config.propertyOrNull("database.user")?.getString() ?: "postgres",
                password = environment.config.propertyOrNull("database.password")?.getString() ?: "postgrespw",
            ).also { db ->
                transaction(db) {
                    SchemaUtils.create(ApprovalTable)
                }
            }
        }

        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<CollectionContract>>() }
        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<CollectionMetadata>>() }
        bindSingleton {
            Cache.Builder().apply {
                expireAfterWrite(72.hours) // TODO: Evict cache smarter?
            }.build<Pair<MsgAddressInt, ULong>, Optional<MsgAddress>>()
        }
        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<ItemContract>>() }
        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<ItemMetadata>>() }
        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<SaleContract>>() }
        bindSingleton { Cache.Builder().build<MsgAddressInt, Optional<RoyaltyContract>>() }

        bindEagerSingleton { ApprovalRepository(di) }
        bindEagerSingleton {
            CollectionRepository(di).apply {
                CoroutineScope(Dispatchers.IO + CoroutineName("collectionRepositoryStartup")).launch {
                    this@apply.all().collect {}
                }
            }
        }
        bindEagerSingleton {
            ItemRepository(di).apply {
                CoroutineScope(Dispatchers.IO + CoroutineName("itemRepositoryStartup")).launch {
                    this@apply.all().collect {}
                }
            }
        }
        bindEagerSingleton { RoyaltyRepository(di) }
    }

    routing {
        controller("/") { StaticController(instance()) }
        controller("/api/v1") { AllCollectionsController(instance()) }
        controller("/api/v1") { AllItemsController(instance()) }
        controller("/api/v1") { CollectionController(instance()) }
        controller("/api/v1") { ItemController(instance()) }
    }
}

