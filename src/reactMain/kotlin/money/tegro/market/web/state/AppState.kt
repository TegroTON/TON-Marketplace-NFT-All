package money.tegro.market.web.state

import money.tegro.market.web.model.Connection
import money.tegro.market.web.model.Dialogue

data class AppState(
    val dialogue: Dialogue = Dialogue.NONE,
    val connection: Connection = Connection(),
)
