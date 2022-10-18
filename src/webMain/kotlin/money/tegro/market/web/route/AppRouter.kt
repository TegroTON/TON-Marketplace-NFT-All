package money.tegro.market.web.route

import dev.fritz2.routing.Router
import kotlinx.browser.window

object AppRouter : Router<Set<String>>(SetRoute(setOf(""))) {
    val navigate = handle<Set<String>> { _, r ->
        window.scrollTo(0.0, 0.0)
        r
    }
}
