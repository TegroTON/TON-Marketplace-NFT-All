package money.tegro.market.web

import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import money.tegro.market.web.context.ConnectionContext
import money.tegro.market.web.dialogue.ConnectDialogue
import money.tegro.market.web.fragment.Footer
import money.tegro.market.web.fragment.Header
import money.tegro.market.web.model.Dialogue
import money.tegro.market.web.page.Collection
import money.tegro.market.web.page.Index
import money.tegro.market.web.page.Item
import money.tegro.market.web.state.AppState
import react.FC
import react.Props
import react.createElement
import react.dom.client.createRoot
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.useState

val mainScope = MainScope()

val App = FC<Props>("App") {
    var appState by useState(AppState())

    ConnectionContext.Provider(appState.connection) {
        Header {
            onConnect = {
                appState = appState.copy(dialogue = Dialogue.CONNECT)
                console.log("yeet")
            }
        }

        HashRouter {
            Routes {
                Route {
                    index = true
                    path = "/"
                    element = createElement(Index)
                }

                Route {
                    path = "/collection/:address"
                    element = createElement(Collection)
                }
                Route {
                    path = "/item/:address"
                    element = createElement(Item)
                }
            }
        }

        Footer {}

        ConnectDialogue {
            open = appState.dialogue == Dialogue.CONNECT
            onClose = {
                appState = appState.copy(dialogue = Dialogue.NONE)
            }
        }
    }
}

fun main() {
    kotlinext.js.require("./index.css")

    val container = document.getElementById("app") ?: error("Couldn't find root container!")

    createRoot(container).render(createElement(App))
}
