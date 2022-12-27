package money.tegro.market.web

import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import dev.fritz2.core.render
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import money.tegro.market.web.fragment.Footer
import money.tegro.market.web.fragment.Header
import money.tegro.market.web.modal.ConnectModal
import money.tegro.market.web.modal.ConnectTonkeeperModal
import money.tegro.market.web.page.*
import money.tegro.market.web.route.AppRouter
import money.tegro.market.web.store.GlobalConnectionStore
import money.tegro.market.web.store.PopOverStore
import money.tegro.market.web.store.TonWalletConnectionStore
import money.tegro.market.web.store.TonkeeperConnectionStore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.conf.global
import org.kodein.di.instance

fun main() {
    kotlinext.js.require("tailwindcss/tailwind.css")

    DI.global.addConfig {
        bindSingleton {
            Json {
                serializersModule = humanReadableSerializerModule
            }
        }

        bindSingleton {
            HttpClient {
                install(ContentNegotiation) {
                    json(instance())
                }
                install(Resources)

                defaultRequest {
                    url("/api/v1/")
                }
            }
        }

        bindSingleton { AppRouter() }
        bindSingleton { GlobalConnectionStore() }
        bindSingleton { TonWalletConnectionStore() }
        bindSingleton { TonkeeperConnectionStore() }
        bindSingleton { PopOverStore() }
    }

    render {
        Header()

        val appRouter: AppRouter by DI.global.instance()
        appRouter.data.render { route ->
            when (route.first()) {
                "" -> Index()
                "collection" -> Collection(route.elementAt(1))
                "item" -> Item(route.elementAt(1))
                "profile" -> Profile(route.elementAt(1))
                "explore" -> Explore()
                else -> NotFound()
            }
        }

        Footer()

        ConnectModal()
        ConnectTonkeeperModal()
        val tonKeeperConnectionStore: TonkeeperConnectionStore by DI.global.instance()
        tonKeeperConnectionStore.restore()
    }
}
