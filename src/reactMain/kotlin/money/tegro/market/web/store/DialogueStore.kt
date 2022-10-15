package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import money.tegro.market.web.model.Dialogue

object DialogueStore : RootStore<Dialogue>(Dialogue.NONE) {
    val connect = handle { _ ->
        Dialogue.CONNECT
    }

    val close = handle { _ ->
        Dialogue.NONE
    }
}
