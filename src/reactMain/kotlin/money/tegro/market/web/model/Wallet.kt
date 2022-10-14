package money.tegro.market.web.model

@OptIn(ExperimentalJsExport::class)
@JsExport
data class Wallet(
    var address: String,
    var publicKey: String,
    var walletVersion: String,
)
