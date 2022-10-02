package money.tegro.market.metadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KLogging
import org.springframework.web.reactive.function.client.WebClient
import org.ton.cell.Cell

@JsonIgnoreProperties(ignoreUnknown = true)
data class CollectionMetadata(
    override val uri: String?,
    override val name: String?,
    override val description: String?,
    override val image: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    override val image_data: ByteArray?,
    val cover_image: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    val cover_image_data: ByteArray?,
) : Metadata {
    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend fun of(content: Cell, webClient: WebClient = WebClient.create()): CollectionMetadata =
            mapper.convertValue(Metadata.of(content, webClient), CollectionMetadata::class.java)
    }
}

