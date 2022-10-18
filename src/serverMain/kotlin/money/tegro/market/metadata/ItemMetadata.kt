package money.tegro.market.metadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KLogging
import org.springframework.web.reactive.function.client.WebClient
import org.ton.cell.Cell

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemMetadata(
    override val uri: String?,
    override val name: String?,
    override val description: String?,
    override val image: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    override val image_data: ByteArray?,
    val attributes: List<ItemMetadataAttribute>? = null,
) : Metadata {
    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend fun of(content: Cell, webClient: WebClient = WebClient.create()): ItemMetadata =
            mapper.convertValue(Metadata.of(content, webClient), ItemMetadata::class.java)
    }
}

