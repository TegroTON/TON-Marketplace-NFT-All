package money.tegro.market.nft

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.Json
import mu.KLogging
import org.ton.cell.Cell
import org.ton.crypto.base64

object ByteArrayAsBase64StringSerializer : KSerializer<ByteArray> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "kotlinx.serialization.ByteArrayAsBase64StringSerializer",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): ByteArray {
        return base64(decoder.decodeString())
    }

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: ByteArray) {
        encoder.encodeString(base64(value))
    }
}

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

                    return HttpClient { }.get(url).bodyAsText()
                        .let {
                            Json { ignoreUnknownKeys = true }.decodeFromString(it)
                        }
                }
                else -> {
                    throw Error("unknown content layout $contentLayout, can't proceed")
                }
            }
        }
    }
}

@Serializable
data class NFTCollectionMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    override val imageData: ByteArray? = null,
    val coverImage: String? = null,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
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


@Serializable
data class NFTItemAttribute(
    @SerialName("trait_type")
    val trait: String,
    val value: String
)

@Serializable
data class NFTItemMetadata(
    override val name: String? = null,
    override val description: String? = null,
    override val image: String? = null,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
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
