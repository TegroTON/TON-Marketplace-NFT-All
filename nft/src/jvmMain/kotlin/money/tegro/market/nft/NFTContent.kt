package money.tegro.market.nft

import io.ipfs.api.IPFS
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.cell.Cell


sealed interface NFTContent {
    abstract val name: String
    abstract val description: String
    abstract val image: String

    companion object : KoinComponent, KLogging() {
        @JvmStatic
        suspend fun parse(raw: Cell): NFTContent {
            logger.debug("parsing ${raw.bits.length} bits of NFT content")

            // concatinate all data from all cells
            val data = raw.bits.toByteArray() + ((raw.treeWalk().map { it.bits.toByteArray() }
                .reduceOrNull { acc, bytes -> acc + bytes }) ?: byteArrayOf())

            if (data.first() == 0x01.toByte()) {
                logger.debug("off-chain content, trying to guess its kind")
                val url = String(data.drop(1).toByteArray())
                if (url.contains("ipfs", ignoreCase = true)) {
                    logger.debug("trying to get IPFS hash from the url: `$url`")

                    val id =
                        Url(url).pathSegments.filter { it.all { it.isLetterOrDigit() } }
                            .sortedBy { it.length }.last() // get longest alpha-numeric part
                    val path = Url(url).pathSegments.takeLastWhile { it != id }.joinToString("/")
                    logger.debug("guess: $id, path = $path")

                    val ipfs: IPFS by inject()

                    return Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<NFTContentOffChainIPFS>(
                        String(
                            ipfs.cat(DummyMultihash(id), path)
                        )
                    ).apply {
                        this._url = url
                    }
                } else {
                    TODO()
                }
            } else {
                TODO()
            }
        }
    }
}

sealed interface NFTContentOffChain : NFTContent {
    abstract val url: String
}

@Serializable
data class NFTContentOffChainIPFS(
    override val name: String,
    override val description: String,
    override val image: String,

    @kotlinx.serialization.Transient
    var _url: String = ""
) : NFTContentOffChain {
    override val url
        get() = _url
}
