package money.tegro.market.web.resource

import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import dev.fritz2.core.IdProvider
import dev.fritz2.repository.Resource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.TransactionRequestDTO

object TransactionRequestResource : Resource<TransactionRequestDTO, String> {
    private val json = Json { serializersModule = humanReadableSerializerModule }

    override val idProvider: IdProvider<TransactionRequestDTO, String> = TransactionRequestDTO::dest
    override fun deserialize(source: String): TransactionRequestDTO = json.decodeFromString(source)
    override fun serialize(item: TransactionRequestDTO): String = json.encodeToString(item)
}
