package money.tegro.market.metadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import mu.KLogging
import org.ton.cell.Cell

@JsonIgnoreProperties(ignoreUnknown = true)
data class CollectionMetadata(
    override val uri: String?,
    override val name: String?,
    override val description: String?,
    override val image: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    override val imageData: ByteArray?,
    val coverImage: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    val coverImageData: ByteArray?,
) : Metadata {
    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend fun of(content: Cell, httpClient: HttpClient = HttpClient {}): CollectionMetadata =
            mapper.convertValue(Metadata.of(content, httpClient), CollectionMetadata::class.java)
    }
}

