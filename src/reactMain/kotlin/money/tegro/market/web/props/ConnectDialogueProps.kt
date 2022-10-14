package money.tegro.market.web.props

import react.Props

external interface ConnectDialogueProps : Props {
    var open: Boolean?

    var onClose: (() -> Unit)?
}
