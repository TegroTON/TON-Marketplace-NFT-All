package money.tegro.market.nft

import io.ipfs.kotlin.IPFS
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import mu.KLogging
import org.ton.cell.Cell

sealed interface NFTCollectionMetadata {
    abstract val name: String
    abstract val description: String
    abstract val image: NFTContent
    abstract val coverImage: NFTContent

    companion object : KLogging() {
        @JvmStatic
        suspend fun of(content: Cell, ipfs: IPFS): NFTCollectionMetadata {
            val contentLayout = content.beginParse().loadUInt(8).toInt()
            if (contentLayout == 0x00) {
                logger.debug { "on-chain content layout detected" }
                TODO("on-chain content layout")
            } else if (contentLayout == 0x01) {
                val rawData = content.bits.toByteArray().drop(1).plus(
                    content.treeWalk().map { it.bits.toByteArray() }.reduceOrNull { acc, bytes -> acc + bytes }
                        ?.toList() ?: listOf()
                ).toByteArray()

                val url = String(rawData)
                logger.debug { "off-chain content layout, url is: $url" }
                if (url.contains("ipfs", ignoreCase = true)) {
                    val id =
                        Url(url).pathSegments.filter { it.all { it.isLetterOrDigit() } }
                            .sortedBy { it.length }.last() // get longest alpha-numeric part

                    logger.debug { "IPFS content detected, its extracted id is: $id" }
                    return Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<NFTCollectionMetadataOffChainIpfs>(ipfs.get.cat(id)).apply {
                        this.id = id
                    }
                } else {
                    return Json {
                        ignoreUnknownKeys = true
                    }.decodeFromStream<NFTCollectionMetadataOffChainHttp>(HttpClient { }.get(url).body()).apply {
                        this.url = url
                    }
                }
            } else {
                throw Error("unknown content layout $contentLayout, can't proceed")
            }
        }
    }
}

sealed interface NFTCollectionMetadataOffChain : NFTCollectionMetadata

@kotlinx.serialization.Serializable
data class NFTCollectionMetadataOffChainHttp(
    override val name: String,
    override val description: String,
    @SerialName("image")
    val imageUrl: String,
    @SerialName("cover_image")
    val coverImageUrl: String,
) : NFTCollectionMetadataOffChain {
    @kotlinx.serialization.Transient
    lateinit var url: String

    @kotlinx.serialization.Transient
    override val image: NFTContentOffChain by lazy { NFTContentOffChain.of(imageUrl) }

    @kotlinx.serialization.Transient
    override val coverImage: NFTContentOffChain by lazy { NFTContentOffChain.of(coverImageUrl) }
}

@kotlinx.serialization.Serializable
data class NFTCollectionMetadataOffChainIpfs(
    override val name: String,
    override val description: String,
    @SerialName("image")
    val imageUrl: String,
    @SerialName("cover_image")
    val coverImageUrl: String,
) : NFTCollectionMetadataOffChain {
    @kotlinx.serialization.Transient
    lateinit var id: String

    @kotlinx.serialization.Transient
    override val image: NFTContentOffChain by lazy { NFTContentOffChain.of(imageUrl) }

    @kotlinx.serialization.Transient
    override val coverImage: NFTContentOffChain by lazy { NFTContentOffChain.of(coverImageUrl) }
}

data class NFTCollectionMetadataOnChain(
    override val name: String,
    override val description: String,
    override val image: NFTContent,
    override val coverImage: NFTContent,
) : NFTCollectionMetadata
