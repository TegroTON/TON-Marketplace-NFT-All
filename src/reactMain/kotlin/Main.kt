import csstype.ClassName
import kotlinx.browser.document
import react.create
import react.dom.DOMAttributes
import react.dom.client.createRoot

fun main() {
    kotlinext.js.require("./index.css")

    val container = document.getElementById("app") ?: error("Couldn't find root container!")

    createRoot(container).render(App.create())
}

var DOMAttributes<*>.classes: String?
    get() = className?.toString()
    set(value) {
        className = if (value != null) ClassName(value) else null
    }
