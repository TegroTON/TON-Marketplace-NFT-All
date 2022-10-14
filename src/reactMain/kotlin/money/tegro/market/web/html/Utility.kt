package money.tegro.market.web.html

import csstype.ClassName
import react.dom.DOMAttributes

var DOMAttributes<*>.classes: String?
    get() = className?.toString()
    set(value) {
        className = if (value != null) ClassName(value) else null
    }
