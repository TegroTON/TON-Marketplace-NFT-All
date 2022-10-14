package money.tegro.market.web.component

import money.tegro.market.web.html.classes
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.props.ButtonProps
import react.FC
import react.dom.html.ButtonType
import react.dom.html.ReactHTML.button

val Button = FC<ButtonProps>("Button") { props ->
    button {
        classes =
            "px-6 py-3 flex flex-nowrap items-center justify-center border rounded-lg text-sm uppercase font-medium tracking-widest"

        when (props.kind) {
            ButtonKind.PRIMARY ->
                classes += " text-dark-900 bg-yellow hover:bg-yellow-hover focus:bg-yellow-hover border-yellow hover:border-yellow-hover focus:border-yellow-hover"

            ButtonKind.SECONDARY ->
                classes += " text-white bg-transparent hover:bg-yellow focus:bg-yellow border-yellow focus:border-yellow"

            ButtonKind.SOFT ->
                classes += " text-white bg-dark-700 hover:bg-gray-900 focus:bg-gray-900 border-dark-700 hover:border-gray-900 focus:border-gray-900"

            else -> {}
        }
        classes += " " + props.classes

        type = ButtonType.button
        onClick = props.onClick

        children = props.children
    }
}
