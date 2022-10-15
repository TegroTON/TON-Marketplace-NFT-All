package money.tegro.market.web

import dev.fritz2.core.RenderContext
import dev.fritz2.core.render
import money.tegro.market.web.dialogue.ConnectDialogue
import money.tegro.market.web.fragment.Footer
import money.tegro.market.web.fragment.Header
import money.tegro.market.web.page.Collection
import money.tegro.market.web.page.Index
import money.tegro.market.web.page.Item
import money.tegro.market.web.route.AppRouter

fun RenderContext.app() {
    Header()

    AppRouter.data.render { route ->
        when (route.first()) {
            "" -> Index()
            "collection" -> Collection(route.last())
            "item" -> Item(route.last())
            else -> div { +"Not Found" }
        }
    }

    Footer()

    ConnectDialogue()
}

fun main() {
    kotlinext.js.require("tailwindcss/tailwind.css")

    render {
        app()
    }
}
