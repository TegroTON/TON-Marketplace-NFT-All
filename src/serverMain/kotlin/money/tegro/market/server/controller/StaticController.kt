package money.tegro.market.server.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import money.tegro.market.server.properties.MarketplaceProperties
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class StaticController(application: Application) : AbstractDIController(application) {
    private val marketplaceProperties: MarketplaceProperties by instance()

    override fun Route.getRoutes() {
        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }

        get("tonconnect-manifest.json") {
            call.respondText(
                "{\n" +
                        "  \"url\": \"https://${if (marketplaceProperties.testnet) "test." else ""}libermall.com\",\n" +
                        "  \"name\": \"Libermall${if (marketplaceProperties.testnet) " Testnet" else ""}\",\n" +
                        "  \"iconUrl\": \"https://libermall.com/assets/img/logo/large.png\"\n" +
                        "}\n",
                ContentType.Application.Json
            )
        }

        static("/") {
            resources("")
        }
    }
}
