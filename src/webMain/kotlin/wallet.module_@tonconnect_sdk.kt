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

external interface `T$0` {
    var tonProof: dynamic /* TonProofItemReplySuccess? | TonProofItemReplyError? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface Wallet {
    var device: DeviceInfo
    var provider: String /* "http" | "injected" */
    var account: Account
    var connectItems: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
}