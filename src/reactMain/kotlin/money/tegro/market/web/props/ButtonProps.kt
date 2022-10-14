package money.tegro.market.web.props

import dom.html.HTMLButtonElement
import money.tegro.market.web.model.ButtonKind
import react.Props
import react.PropsWithChildren
import react.dom.events.MouseEventHandler

external interface ButtonProps : Props, PropsWithChildren {
    var kind: ButtonKind?

    var classes: String?
    var onClick: MouseEventHandler<HTMLButtonElement>?
}
