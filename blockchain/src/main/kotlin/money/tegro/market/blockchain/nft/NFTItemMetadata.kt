package money.tegro.market.blockchain.nft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import mu.KLogging
import org.ton.block.AddrNone
import org.ton.block.MsgAddress
import org.ton.cell.Cell

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTItemMetadata(
    override val address: MsgAddress = AddrNone,
    override val name: String?,
    override val description: String?,
    override val image: String?,
    @JsonSerialize(using = ByteArraySerializer::class)
    override val imageData: ByteArray?,
    val attributes: List<NFTItemMetadataAttribute>? = null,
) : NFTMetadata() {
    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend fun of(
            address: MsgAddress,
            content: Cell,
            httpClient: HttpClient = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                }
                install(HttpRequestRetry) {
                    maxRetries = 10
                    retryIf { request, response ->
                        response.contentType() != ContentType.parse("application/json")
                    }
                }
            }
        ) = mapper.readValue(parseContent(content, httpClient), NFTItemMetadata::class.java).copy(address = address)
    }
}
