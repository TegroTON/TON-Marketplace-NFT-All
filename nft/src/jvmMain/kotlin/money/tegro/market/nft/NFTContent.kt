package money.tegro.market.nft

import io.ipfs.kotlin.IPFS
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KLogging
import org.ton.cell.Cell

interface NFTContent {
    abstract val name: String
    abstract val description: String
    abstract val image: String
}

fun isOnChainContent(raw: Cell) = raw.beginParse().loadUInt(8).toInt() == 0x00

@Serializable
data class NFTContentOffChain(
    override val name: String,
    override val description: String,
    override val image: String,
) : NFTContent {
    companion object : KLogging() {
        @JvmStatic
        suspend fun parse(ipfs: IPFS, raw: Cell): NFTContent {
            require(!isOnChainContent(raw))

            val data = (raw.bits.toByteArray() + ((raw.treeWalk().map { it.bits.toByteArray() }
                .reduceOrNull { acc, bytes -> acc + bytes }) ?: byteArrayOf())).drop(1).toByteArray()

            logger.debug("off-chain content, trying to guess its kind")
            val url = String(data)
            if (url.contains("ipfs", ignoreCase = true)) {
                logger.debug("trying to get IPFS hash from the url: `$url`")

                val id =
                    Url(url).pathSegments.filter { it.all { it.isLetterOrDigit() } }
                        .sortedBy { it.length }.last() // get longest alpha-numeric part
                val path = Url(url).pathSegments.takeLastWhile { it != id }.joinToString("/")
                logger.debug("guess: $id, path = $path")

                return Json { ignoreUnknownKeys = true }.decodeFromString<NFTContentOffChain>(ipfs.get.cat(id))
            } else { // regular http
                logger.debug("trying to get $url")

                return Json { ignoreUnknownKeys = true }.decodeFromString<NFTContentOffChain>(
                    HttpClient().get(url).bodyAsText()
                )
            }
        }
    }
}
