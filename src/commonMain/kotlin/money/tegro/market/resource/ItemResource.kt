package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/items")
data class ItemResource(
    val sort: Sort = Sort.ALL,
    val drop: Int? = null,
    val take: Int? = null,
) {
    @Serializable
    enum class Sort {
        ALL,
    }

    @Serializable
    @Resource("/{address}")
    data class ByAddress(
        val parent: ItemResource = ItemResource(),
        val address: String,
    ) {
        @Serializable
        @Resource("/transfer")
        data class Transfer(
            val parent: ByAddress,
            val newOwner: String,
            val response: String? = null,
        )

        @Serializable
        @Resource("/sell")
        data class Sell(
            val parent: ByAddress,
            val seller: String,
            val price: String,
        )
    }

    @Serializable
    @Resource("/of/{address}")
    data class ByRelation(
        val parent: ItemResource = ItemResource(),
        val address: String,
        val relation: Relation = ByRelation.Relation.COLLECTION,
        val sortItems: Sort? = null,
        val sortReverse: Boolean? = null,
        val drop: Int? = null,
        val take: Int? = null,
    ) {
        @Serializable
        enum class Relation {
            COLLECTION,
            OWNED,
        }

        @Serializable
        enum class Sort {
            INDEX,
            PRICE,
        }

        @Serializable
        @Resource("/attributes")
        data class Attributes(
            val parent: ByRelation,
        )
    }
}
