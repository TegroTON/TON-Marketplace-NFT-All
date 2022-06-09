package money.tegro.market.nft

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KLogging
import org.ton.cell.Cell

interface NFTMetadata {
    val name: String?
    val description: String?
    val image: String?
    val imageData: ByteArray?

    companion object : KLogging() {
        @JvmStatic
        suspend inline fun <reified T : NFTMetadata> of(
            content: Cell,
        ): T {
            val contentLayout = content.beginParse().loadUInt(8).toInt()
            if (contentLayout == 0x00) {
                logger.debug { "on-chain content layout detected" }
                TODO("on-chain content layout")
            } else if (contentLayout == 0x01) {
                val rawData = content.bits.toByteArray().drop(1).plus(
                    content.treeWalk().map { it.bits.toByteArray() }
                        .reduceOrNull { acc, bytes -> acc + bytes }
                        ?.toList() ?: listOf()
                ).toByteArray()

                val url = String(rawData)
                logger.debug { "off-chain content layout, url is: $url" }

                return HttpClient { }.get(url).bodyAsText()
                    .let {
                        Json { ignoreUnknownKeys = true }.decodeFromString<T>(it)
                    }
            } else {
                throw Error("unknown content layout $contentLayout, can't proceed")
            }
        }
    }
}

@kotlinx.serialization.Serializable
data class NFTCollectionMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    override val imageData: ByteArray? = null,
    val coverImage: String? = null,
    val coverImageData: ByteArray? = null,
) : NFTMetadata


@kotlinx.serialization.Serializable
data class NFTItemAttribute(
    @SerialName("trait_type")
    val trait: String,
    val value: String
)

@kotlinx.serialization.Serializable
data class NFTItemMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    override val imageData: ByteArray? = null,
    val attributes: List<NFTItemAttribute>? = null,
) : NFTMetadata
