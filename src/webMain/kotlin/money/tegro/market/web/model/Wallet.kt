package money.tegro.market.web.model

@OptIn(ExperimentalJsExport::class)
@JsExport
external interface Wallet {
    val address: String
    val publicKey: String
    val walletVersion: String
}
