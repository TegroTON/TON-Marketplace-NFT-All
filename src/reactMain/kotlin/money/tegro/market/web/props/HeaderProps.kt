package money.tegro.market.web.props

import react.Props

external interface HeaderProps : Props {
    var onConnect: (() -> Unit)?
}
