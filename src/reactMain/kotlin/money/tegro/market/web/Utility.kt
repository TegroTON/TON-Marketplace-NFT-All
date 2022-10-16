package money.tegro.market.web

import com.ionspin.kotlin.bignum.integer.BigInteger

fun BigInteger.formatTON() = this.toString()
    .let {
        it.dropLast(9).ifEmpty { "0" } + "." +
                it.takeLast(9).padStart(9, '0').dropLastWhile { it == '0' }
    }

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}
