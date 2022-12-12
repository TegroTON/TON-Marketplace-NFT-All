@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS"
)

import kotlin.js.Promise

@JsModule("@tonconnect/sdk")
@JsNonModule
external open class TonConnect(options: TonConnectOptions = definedExternally) : ITonConnect {
    override var connected: Boolean
    override var account: Account?
    override var wallet: Wallet?
    open val walletsList: Any
    open val dappSettings: Any
    open val bridgeConnectionStorage: Any
    open var _wallet: Any
    open var provider: Any
    open var statusChangeSubscriptions: Any
    open var statusChangeErrorSubscriptions: Any
    override fun getWallets(): Promise<Array<dynamic /* WalletInfoRemote | WalletInfoInjected | WalletInfoRemote & WalletInfoInjected */>>
    override fun onStatusChange(
        callback: (wallet: Wallet?) -> Unit,
        errorsHandler: (err: TonConnectError) -> Unit
    ): () -> Unit

    override fun <T> connect(wallet: T, request: ConnectAdditionalRequest): Any
    override fun restoreConnection(): Promise<Unit>
    override fun sendTransaction(transaction: SendTransactionRequest): Promise<SendTransactionResponse>
    override fun disconnect(): Promise<Unit>
    open var createProvider: Any
    open var walletEventsListener: Any
    open var onWalletConnected: Any
    open var onWalletConnectError: Any
    open var onWalletDisconnected: Any
    open var checkConnection: Any
    open var checkFeatureSupport: Any
    open var createConnectRequest: Any
}
