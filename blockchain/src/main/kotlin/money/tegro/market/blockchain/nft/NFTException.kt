package money.tegro.market.blockchain.nft

class NFTException(override val message: String?, override val cause: Throwable? = null) : Exception(message, cause)
