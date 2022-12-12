package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import money.tegro.market.web.model.PopOver
import org.kodein.di.conf.DIGlobalAware

class PopOverStore : RootStore<PopOver>(PopOver.NONE), DIGlobalAware {
    val menu = handle { if (current != PopOver.MENU) PopOver.MENU else PopOver.NONE }
    val connect = handle { if (current != PopOver.CONNECT) PopOver.CONNECT else PopOver.NONE }
    val connectTonkeeper =
        handle { if (current != PopOver.CONNECT_TONKEEPER) PopOver.CONNECT_TONKEEPER else PopOver.NONE }
    val transfer = handle { if (current != PopOver.TRANSFER) PopOver.TRANSFER else PopOver.NONE }
    val sell = handle { if (current != PopOver.SELL) PopOver.SELL else PopOver.NONE }
    val cancelSale = handle { if (current != PopOver.CANCEL_SALE) PopOver.CANCEL_SALE else PopOver.NONE }
    val buy = handle { if (current != PopOver.BUY) PopOver.BUY else PopOver.NONE }
    val close = handle { PopOver.NONE }
}
