package money.tegro.market.web.store.tonwallet

import kotlin.js.Promise

external interface TonWalletProvider {
    var isTonWallet: Boolean;
    fun send(method: String, params: Array<Any>?): Promise<Any>;
    fun on(eventName: String, handler: (data: Array<Any>) -> Any): Unit;
}
