package money.tegro.market.web.resource

import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import dev.fritz2.core.IdProvider
import dev.fritz2.repository.Resource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.ItemDTO

object ItemResource : Resource<ItemDTO, String> {
    private val json = Json { serializersModule = humanReadableSerializerModule }

    override val idProvider: IdProvider<ItemDTO, String> = ItemDTO::address
    override fun deserialize(source: String): ItemDTO = json.decodeFromString(source)
    override fun serialize(item: ItemDTO): String = json.encodeToString(item)
    override fun deserializeList(source: String): List<ItemDTO> = json.decodeFromString(source)
    override fun serializeList(items: List<ItemDTO>): String = json.encodeToString(items)
}
