package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import money.tegro.market.web.model.PopOver

object PopOverStore : RootStore<PopOver>(PopOver.NONE) {
    val menu = handle { if (current != PopOver.MENU) PopOver.MENU else PopOver.NONE }
    val connect = handle { if (current != PopOver.CONNECT) PopOver.CONNECT else PopOver.NONE }
    val close = handle { PopOver.NONE }
}
