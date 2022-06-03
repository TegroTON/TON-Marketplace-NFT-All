package money.tegro.market.nft

sealed interface NFTContent

sealed interface NFTContentOffChain : NFTContent

data class NFTContentOffChainHttp(
    val url: String
) : NFTContentOffChain

data class NFTContentOffChainIpfs(
    val id: String
) : NFTContentOffChain

data class NFTContentOnChain(
    val data: ByteArray
) : NFTContent

