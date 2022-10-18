package money.tegro.market.web.route

import dev.fritz2.routing.Route

class SetRoute(override val default: Set<String>) : Route<Set<String>> {
    private val separator = "/"
    override fun deserialize(hash: String): Set<String> = hash.split(separator).toSet()
    override fun serialize(route: Set<String>): String = route.joinToString(separator)
}
