package money.tegro.market.web.resource

import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import dev.fritz2.core.IdProvider
import dev.fritz2.repository.Resource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.CollectionDTO

object CollectionResource : Resource<CollectionDTO, String> {
    private val json = Json { serializersModule = humanReadableSerializerModule }

    override val idProvider: IdProvider<CollectionDTO, String> = CollectionDTO::address
    override fun deserialize(source: String): CollectionDTO = json.decodeFromString(source)
    override fun serialize(item: CollectionDTO): String = json.encodeToString(item)
    override fun deserializeList(source: String): List<CollectionDTO> = json.decodeFromString(source)
    override fun serializeList(items: List<CollectionDTO>): String = json.encodeToString(items)
}
