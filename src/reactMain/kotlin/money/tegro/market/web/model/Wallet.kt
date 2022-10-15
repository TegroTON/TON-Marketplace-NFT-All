package money.tegro.market.web.model

@OptIn(ExperimentalJsExport::class)
@JsExport
data class Wallet(
    val address: String,
    val publicKey: String,
    val walletVersion: String,
)
