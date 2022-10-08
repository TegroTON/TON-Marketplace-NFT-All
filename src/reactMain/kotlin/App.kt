import components.AppFooter
import components.AppHeader
import pages.Collection
import pages.Index
import react.FC
import react.Props
import react.createElement
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter

val App = FC<Props>("App") {
    AppHeader { }
    HashRouter {
        Routes {
            Route {
                index = true
                element = createElement(Index)
            }
            Route {
                path = "/collection"
                element = createElement(Collection)
            }
        }
    }
    AppFooter { }
}
