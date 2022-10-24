package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/item/{address}")
data class ItemResource(val address: String) {
    @Serializable
    @Resource("/transfer")
    data class Transfer(
        val parent: ItemResource,
        val newOwner: String,
        val response: String? = null,
    )

    @Serializable
    @Resource("/sell")
    data class Sell(
        val parent: ItemResource,
        val seller: String,
        val price: String,
    )
}
