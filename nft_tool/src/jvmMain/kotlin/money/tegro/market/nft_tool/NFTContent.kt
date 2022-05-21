package money.tegro.market.nft_tool

import io.ipfs.api.IPFS
import io.ipfs.multihash.Multihash
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.ton.cell.Cell

sealed interface NFTContent {
    companion object {
        @JvmStatic
        fun parse(raw: Cell): NFTContent {
            val data = raw.bits.toByteArray()
            if (data.first() == 0x01.toByte()) {
                // off-chain
                return NFTContentOffChain.fetch(String(data.drop(1).toByteArray()))
            } else {
                TODO()
            }
        }
    }
}

sealed interface NFTContentOffChain : NFTContent {
    companion object {
        @JvmStatic
        fun fetch(url: String): NFTContentOffChain = fetch(Url(url))

        @JvmStatic
        fun fetch(url: Url): NFTContentOffChain {
            if (url.host == "cloudflare-ipfs.com") {
                return NFTContentOffChainIPFS.fetch(url.pathSegments.last())
            } else {
                TODO()
            }
        }
    }
}

@Serializable
data class NFTContentOffChainIPFS(
    val name: String,
    val description: String,
    val image: String,
    val imageData: String? = null,
) : NFTContentOffChain {
    companion object {
        @JvmStatic
        fun fetch(id: String): NFTContentOffChainIPFS {
            val ipfs = IPFS("/ip4/127.0.0.1/tcp/5001")
            return Json {
                ignoreUnknownKeys = true
            }.decodeFromString<NFTContentOffChainIPFS>(String(ipfs.cat(Multihash.fromBase58(id))))
        }
    }
}