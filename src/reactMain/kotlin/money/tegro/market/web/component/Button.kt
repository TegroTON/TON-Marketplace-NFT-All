package money.tegro.market.web.component

import dev.fritz2.core.RenderContext
import dev.fritz2.core.type
import money.tegro.market.web.model.ButtonKind

fun RenderContext.Button(
    classes: String = "",
    kind: ButtonKind = ButtonKind.PRIMARY,
    content: RenderContext.() -> Unit
) {
    button("px-6 py-3 flex flex-nowrap items-center justify-center border rounded-lg text-sm uppercase font-medium tracking-widest") {
        when (kind) {
            ButtonKind.PRIMARY ->
                className("text-dark-900 bg-yellow hover:bg-yellow-hover focus:bg-yellow-hover border-yellow hover:border-yellow-hover focus:border-yellow-hover")

            ButtonKind.SECONDARY ->
                className("text-white bg-transparent hover:bg-yellow focus:bg-yellow border-yellow focus:border-yellow")

            ButtonKind.SOFT ->
                className("text-white bg-dark-700 hover:bg-gray-900 focus:bg-gray-900 border-dark-700 hover:border-gray-900 focus:border-gray-900")
        }
        
        className(classes)
        type("button")

        content()
    }
}
