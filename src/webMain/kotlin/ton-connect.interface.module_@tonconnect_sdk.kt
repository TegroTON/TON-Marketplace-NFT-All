@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface ITonConnect {
    var connected: Boolean
    var account: Account?
    var wallet: Wallet?
    fun getWallets(): Promise<Array<dynamic /* WalletInfoRemote | WalletInfoInjected | WalletInfoRemote & WalletInfoInjected */>>
    fun onStatusChange(callback: (walletInfo: Wallet?) -> Unit, errorsHandler: (err: TonConnectError) -> Unit = definedExternally): () -> Unit
    fun <T> connect(wallet: T, request: ConnectAdditionalRequest = definedExternally): Any
    fun restoreConnection(): Promise<Unit>
    fun disconnect(): Promise<Unit>
    fun sendTransaction(transaction: SendTransactionRequest): Promise<SendTransactionResponse>
}