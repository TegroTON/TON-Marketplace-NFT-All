package money.tegro.market.nft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import org.ton.cell.Cell

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTMetadataAttribute(
    @JsonProperty("trait_type")
    val trait: String,
    val value: String
)

// Probably bad design to mix metadata for both collections, both off- and on-chain in one data class but
// don't care + didn't ask + cry about it + stay mad + get real + mald seethe +
// cope harder + hoes mad + skill issue + ratio + you fell off + the audacity +
// triggered + any askers + redpilled + get a life + ok and? + cringe + touch
// grass + donowalled + not based
@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTMetadata(
    val name: String?,
    val description: String?,
    val image: String?,
    val imageData: ByteArray?,
    val coverImage: String? = null,
    val coverImageData: ByteArray? = null,

    val attributes: List<NFTMetadataAttribute>? = null,
) {
    companion object : KLogging() {
        val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend fun of(
            content: Cell,
            httpClient: HttpClient = HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                }
            }
        ): NFTMetadata {
            when (val contentLayout = content.beginParse().loadUInt(8).toInt()) {
                0x00 -> {
                    logger.debug { "on-chain content layout detected" }
                    TODO("on-chain content layout, really?")
                }
                0x01 -> {
                    val rawData = content.bits.toByteArray().drop(1).plus(
                        content.treeWalk().map { it.bits.toByteArray() }
                            .reduceOrNull { acc, bytes -> acc + bytes }
                            ?.toList() ?: listOf()
                    ).toByteArray()

                    val url = String(rawData)
                    logger.debug { "off-chain content layout, url is: $url" }

                    return mapper.readValue(httpClient.get(url).bodyAsText(), NFTMetadata::class.java)
                }
                else -> {
                    throw Error("unknown content layout $contentLayout, can't proceed")
                }
            }
        }
    }
}
