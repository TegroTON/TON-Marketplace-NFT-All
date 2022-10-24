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
import money.tegro.market.web.page.Collection
import money.tegro.market.web.page.Index
import money.tegro.market.web.page.Item
import money.tegro.market.web.page.Profile
import money.tegro.market.web.route.AppRouter

val client = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = humanReadableSerializerModule
            }
        )
    }
    install(Resources)

    defaultRequest {
        url("http://localhost:8080/api/v1/")
    }
}

fun main() {
    kotlinext.js.require("tailwindcss/tailwind.css")

    render {
        Header()

        AppRouter.data.render { route ->
            when (route.first()) {
                "" -> Index()
                "collection" -> Collection(route.last())
                "item" -> Item(route.last())
                "profile" -> Profile(route.last())
                else -> div { +"Not Found" }
            }
        }

        Footer()

        ConnectModal()
    }
}
