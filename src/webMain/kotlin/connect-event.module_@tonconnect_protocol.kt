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

external interface `T$1` {
    var lengthBytes: Number
    var value: String
}

external interface `T$2` {
    var timestamp: Number
    var domain: `T$1`
    var payload: String
    var signature: String
}

external interface TonProofItemReplySuccess {
    var name: String /* "ton_proof" */
    var proof: `T$2`
}

typealias TonProofItemReplyError = ConnectItemReplyError<String /* "ton_proof" */>

external enum class CONNECT_ITEM_ERROR_CODES {
    UNKNOWN_ERROR /* = 0 */,
    METHOD_NOT_SUPPORTED /* = 400 */
}

external interface `T$3` {
    var code: CONNECT_ITEM_ERROR_CODES
    var message: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConnectItemReplyError<T> {
    var name: T
    var error: `T$3`
}