package money.tegro.market.web.store.tonwallet

@OptIn(ExperimentalJsExport::class)
@JsExport
external interface Wallet {
    val address: String
    val publicKey: String
    val walletVersion: String
}
