package money.tegro.market.metadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.ton.bitstring.BitString
import org.ton.cell.Cell
import org.ton.crypto.sha256
import org.ton.smartcontract.*
import org.ton.tlb.loadTlb

@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface Metadata {
    val uri: String?
    val name: String?
    val description: String?
    val image: String?
    val imageData: ByteArray?

    companion object : KLogging() {
        private val mapper by lazy { jacksonObjectMapper() }

        val ONCHAIN_URI_KEY = BitString(sha256("uri".toByteArray()))
        val ONCHAIN_NAME_KEY = BitString(sha256("name".toByteArray()))
        val ONCHAIN_DESCRIPTION_KEY = BitString(sha256("description".toByteArray()))
        val ONCHAIN_IMAGE_KEY = BitString(sha256("image".toByteArray()))
        val ONCHAIN_IMAGE_DATA_KEY = BitString(sha256("image_data".toByteArray()))

        @JvmStatic
        suspend fun of(content: Cell, httpClient: HttpClient): JsonNode {
            return when (val full = content.parse { loadTlb(FullContent) }) {
                is FullContent.OnChain -> {
                    logger.debug { "on-chain content layout, frick" }
                    val entries = full.data.toMap()

                    mapper.createObjectNode()
                        // TODO: Semi-on-chain content
                        .put("uri", entries.get(ONCHAIN_URI_KEY)?.flatten()?.decodeToString())
                        .put("name", entries.get(ONCHAIN_NAME_KEY)?.flatten()?.decodeToString())
                        .put("description", entries.get(ONCHAIN_DESCRIPTION_KEY)?.flatten()?.decodeToString())
                        .put("image", entries.get(ONCHAIN_IMAGE_KEY)?.flatten()?.decodeToString())
                        .put("image_data", entries.get(ONCHAIN_IMAGE_DATA_KEY)?.flatten())
                }
                is FullContent.OffChain -> {
                    logger.debug { "off-chain content layout, thanks god" }

                    val url = String(full.uri.data.flatten())
                    logger.debug("content url is {}", StructuredArguments.v("url", url))

                    mapper.readTree(httpClient.get(url).bodyAsText())
                }
            }
        }
    }
}

private fun SnakeData.flatten(): ByteArray = when (this) {
    is SnakeDataTail -> bits.toByteArray()
    is SnakeDataCons -> bits.toByteArray() + next.flatten()
}

private fun ContentData.flatten(): ByteArray = when (this) {
    is ContentData.Snake -> this.data.flatten()
    is ContentData.Chunks -> TODO("chunky content data")
}
