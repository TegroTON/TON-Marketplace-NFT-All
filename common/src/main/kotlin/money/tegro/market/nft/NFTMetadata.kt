package money.tegro.market.nft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import org.ton.cell.Cell

interface NFTMetadata {
    val name: String?
    val description: String?
    val image: String?
    val imageData: ByteArray?

    companion object : KLogging() {
        val mapper by lazy { jacksonObjectMapper() }

        @JvmStatic
        suspend inline fun <reified T : NFTMetadata> of(
            content: Cell,
        ): T {
            val contentLayout = content.beginParse().loadUInt(8).toInt()
            when (contentLayout) {
                0x00 -> {
                    logger.debug { "on-chain content layout detected" }
                    TODO("on-chain content layout")
                }
                0x01 -> {
                    val rawData = content.bits.toByteArray().drop(1).plus(
                        content.treeWalk().map { it.bits.toByteArray() }
                            .reduceOrNull { acc, bytes -> acc + bytes }
                            ?.toList() ?: listOf()
                    ).toByteArray()

                    val url = String(rawData)
                    logger.debug { "off-chain content layout, url is: $url" }

                    return mapper.readValue(HttpClient {}.get(url).bodyAsText(), T::class.java)
                }
                else -> {
                    throw Error("unknown content layout $contentLayout, can't proceed")
                }
            }
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTCollectionMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    override val imageData: ByteArray? = null,
    val coverImage: String? = null,
    val coverImageData: ByteArray? = null,
) : NFTMetadata {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NFTCollectionMetadata

        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false
        if (coverImage != other.coverImage) return false
        if (coverImageData != null) {
            if (other.coverImageData == null) return false
            if (!coverImageData.contentEquals(other.coverImageData)) return false
        } else if (other.coverImageData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        result = 31 * result + (coverImage?.hashCode() ?: 0)
        result = 31 * result + (coverImageData?.contentHashCode() ?: 0)
        return result
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTItemAttribute(
    @JsonProperty("trait_type")
    val trait: String,
    val value: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTItemMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    override val imageData: ByteArray? = null,
    val attributes: List<NFTItemAttribute>? = null,
) : NFTMetadata {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NFTItemMetadata

        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false
        if (attributes != other.attributes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        result = 31 * result + (attributes?.hashCode() ?: 0)
        return result
    }
}
