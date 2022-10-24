package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/collections")
data class AllCollectionsResource(
    val sort: Sort? = null,
    val drop: Int? = null,
    val take: Int? = null,
) {
    @Serializable
    enum class Sort {
        ALL,
        TOP,
    }
}
