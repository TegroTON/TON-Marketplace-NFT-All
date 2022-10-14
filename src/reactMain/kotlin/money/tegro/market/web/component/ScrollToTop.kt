package money.tegro.market.web.component

import browser.window
import react.FC
import react.Props
import react.router.useLocation
import react.useEffect

val ScrollToTop = FC<Props>("ScrollToTop") {
    val location = useLocation()

    useEffect(location) {
        window.scrollTo(0.0, 0.0)
    }
}
