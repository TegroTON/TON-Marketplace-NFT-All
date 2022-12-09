package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/collection/")
class CollectionResource {
    @Serializable
    @Resource("/all")
    data class All(
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

    @Serializable
    @Resource("/address/{address}")
    data class ByAddress(
        val address: String,
    )
}
