package money.tegro.market.nft

import io.ktor.http.*

sealed interface NFTContent

sealed interface NFTContentOffChain : NFTContent {
    companion object {
        @JvmStatic
        fun of(url: String): NFTContentOffChain {
            if (url.contains("ipfs", ignoreCase = true)) {
                return NFTContentOffChainIpfs(
                    Url(url).pathSegments.filter { it.all { it.isLetterOrDigit() } }
                        .sortedBy { it.length }.last()
                ) // get longest alpha-numeric part
            } else {
                return NFTContentOffChainHttp(url)
            }
        }
    }
}

data class NFTContentOffChainHttp(
    val url: String
) : NFTContentOffChain

data class NFTContentOffChainIpfs(
    val id: String
) : NFTContentOffChain

data class NFTContentOnChain(
    val data: ByteArray
) : NFTContent

