package money.tegro.market.nightcrawler

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.context.scope.Refreshable
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

@Refreshable
class ReferenceBlock(private val context: ApplicationContext) {
    private lateinit var referenceBlock: TonNodeBlockIdExt

    fun get() = suspend {
        if (!::referenceBlock.isInitialized) {
            referenceBlock = context.getBean(LiteApi::class.java).getMasterchainInfo().last
        }
        referenceBlock
    }
}
