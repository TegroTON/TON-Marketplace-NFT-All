package money.tegro.market.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/collection/{address}")
data class CollectionResource(
    val address: String,
)
