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
    @Resource("/collection/{collection}")
    data class ByCollection(
        val parent: ItemResource = ItemResource(),
        val collection: String,
        val sortItems: Sort = Sort.INDEX,
        val drop: Int? = null,
        val take: Int? = null,
    ) {
        @Serializable
        enum class Sort {
            INDEX,
        }
    }

    @Serializable
    @Resource("/profile/{collection}")
    data class ByOwner(
        val parent: ItemResource = ItemResource(),
        val owner: String,
        val sortItems: Sort = Sort.INDEX,
        val drop: Int? = null,
        val take: Int? = null,
    ) {
        @Serializable
        enum class Sort {
            INDEX,
        }
    }
}
