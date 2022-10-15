package money.tegro.market.web.route

import dev.fritz2.routing.Router

object AppRouter : Router<Set<String>>(SetRoute(setOf("")))
