package money.tegro.market.web.resource

import dev.fritz2.core.IdProvider
import dev.fritz2.repository.Resource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.web.model.Connection

object ConnectionResource : Resource<Connection, String> {
    override val idProvider: IdProvider<Connection, String> = Connection::id
    override fun deserialize(source: String): Connection = Json.decodeFromString(source)
    override fun serialize(item: Connection): String = Json.encodeToString(item)
}
