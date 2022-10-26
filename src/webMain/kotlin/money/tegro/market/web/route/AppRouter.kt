package money.tegro.market.web.route

import dev.fritz2.routing.Router
import kotlinx.browser.window
import org.kodein.di.conf.DIGlobalAware

class AppRouter : Router<Set<String>>(SetRoute(setOf(""))), DIGlobalAware {
    val navigate = handle<Set<String>> { _, r ->
        window.scrollTo(0.0, 0.0)
        r
    }
}
