import components.AppFooter
import components.AppHeader
import kotlinx.coroutines.MainScope
import pages.Collection
import pages.Index
import pages.Item
import react.FC
import react.Props
import react.createElement
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter

val mainScope = MainScope()

val App = FC<Props>("App") {
    AppHeader { }
    HashRouter {
        Routes {
            Route {
                index = true
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
    AppFooter { }
}
