package money.tegro.market.web.model

data class Wallet(
    var address: String,
    var publicKey: String,
    var walletVersion: String,
)
