package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/collections")
data class CollectionResource(
    val sort: Sort = Sort.ALL,
) {
    @Serializable
    enum class Sort {
        ALL,
        TOP,
    }

    @Serializable
    @Resource("/{address}")
    data class ByAddress(
        val parent: CollectionResource = CollectionResource(),
        val address: String,
    )
}
