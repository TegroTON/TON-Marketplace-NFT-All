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

external interface WalletInfoBase {
    var name: String
    var imageUrl: String
    var tondns: String?
        get() = definedExternally
        set(value) = definedExternally
    var aboutUrl: String
}

external interface WalletInfoRemote : WalletInfoBase {
    var universalLink: String
    var bridgeUrl: String
}

external interface WalletInfoInjected : WalletInfoBase {
    var jsBridgeKey: String
    var injected: Boolean
    var embedded: Boolean
}

external fun isWalletInfoInjected(value: WalletInfoRemote /* WalletInfoRemote & WalletInfoInjected */): Boolean

external fun isWalletInfoInjected(value: WalletInfoInjected): Boolean