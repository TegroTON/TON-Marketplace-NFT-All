package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/items")
data class ItemResource(
    val sort: Sort = Sort.ALL,
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
        val sort: Sort = Sort.INDEX
    ) {
        @Serializable
        enum class Sort {
            INDEX,
        }
    }
}
