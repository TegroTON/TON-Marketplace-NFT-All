package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/items")
class AllItemsResource(
    val sort: Sort? = null,
    val relatedTo: String? = null,
    val relation: Relation? = null,
    val filter: Filter? = null,
    val drop: Int? = null,
    val take: Int? = null,
) {
    @Serializable
    enum class Sort {
        INDEX_UP,
        INDEX_DOWN,
        PRICE_UP,
        PRICE_DOWN,
    }

    @Serializable
    enum class Relation {
        COLLECTION,
        OWNERSHIP,
    }

    @Serializable
    enum class Filter {
        ON_SALE,
        NOT_FOR_SALE,
    }
}
