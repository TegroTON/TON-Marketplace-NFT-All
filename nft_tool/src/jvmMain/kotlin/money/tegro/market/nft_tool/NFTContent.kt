package money.tegro.market.nft_tool

import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.cell.Cell

abstract class NFTContent {
    abstract val name: String
    abstract val description: String
    abstract val image: String

    companion object : KLogging() {
        @JvmStatic
        fun parse(raw: Cell): NFTContent {
            logger.debug("parsing ${raw.bits.length} bits of NFT content")
            val data = raw.bits.toByteArray()
            if (data.first() == 0x01.toByte()) {
                logger.debug("off-chain content")
                return NFTContentOffChain.fetch(String(data.drop(1).toByteArray()))
            } else {
                TODO()
            }
        }
    }
}

abstract class NFTContentOffChain : NFTContent() {
    abstract var url: String

    companion object : KLogging() {
        @JvmStatic
        fun fetch(url: String): NFTContentOffChain {
            logger.debug("attempting to guess off-chain content kind")
            if (url.contains("ipfs", ignoreCase = true)) {
                return NFTContentOffChainIPFS.fetch(url)
            } else {
                TODO()
            }
        }
    }
}

@Serializable
data class NFTContentOffChainIPFS(
    override val name: String,
    override val description: String,
    override val image: String,

    @kotlinx.serialization.Transient
    override var url: String = "",
    @kotlinx.serialization.Transient
    var id: String = "",
) : NFTContentOffChain() {

    companion object : KoinComponent, KLogging() {
        @JvmStatic
        fun fetch(url: String): NFTContentOffChainIPFS {
            logger.debug("trying to get IPFS hash from the url: `$url`")

            val id =
                Url(url).pathSegments.filter { it.all { it.isLetterOrDigit() } }
                    .sortedBy { it.length }.last()
            logger.debug("guess: $id")

            val ipfs: IPFS by inject()
            return Json {
                ignoreUnknownKeys = true
            }.decodeFromString<NFTContentOffChainIPFS>(String(ipfs.cat(Multihash.fromBase58(id)))).apply {
                this.id = id
                this.url = url
            }
        }
    }
}