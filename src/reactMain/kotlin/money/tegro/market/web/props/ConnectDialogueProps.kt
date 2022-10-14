package money.tegro.market.web.props

import money.tegro.market.web.model.Connection
import react.Props

external interface ConnectDialogueProps : Props {
    var open: Boolean?

    var onClose: (() -> Unit)?
    var onConnect: ((connection: Connection) -> Unit)?
}
