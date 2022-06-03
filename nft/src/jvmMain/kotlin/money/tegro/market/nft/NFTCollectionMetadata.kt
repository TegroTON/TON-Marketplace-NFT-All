package money.tegro.market.nft

sealed interface NFTCollectionMetadata {
    abstract val name: String
    abstract val description: String
    abstract val image: NFTContent
    abstract val coverImage: NFTContent
}

sealed interface NFTCollectionMetadataOffChain : NFTCollectionMetadata

data class NFTCollectionMetadataOffChainHttp(
    override val name: String,
    override val description: String,
    override val image: NFTContentOffChain,
    override val coverImage: NFTContentOffChain,
    val url: String,
) : NFTCollectionMetadataOffChain

data class NFTCollectionMetadataOffChainIpfs(
    override val name: String,
    override val description: String,
    override val image: NFTContentOffChain,
    override val coverImage: NFTContentOffChain,
    val id: String,
) : NFTCollectionMetadataOffChain

data class NFTCollectionMetadataOnchain(
    override val name: String,
    override val description: String,
    override val image: NFTContent,
    override val coverImage: NFTContent,
) : NFTCollectionMetadata
