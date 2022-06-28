package money.tegro.market.nightcrawler

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.runtime.context.scope.Refreshable
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

sealed interface ReferenceBlock {
    operator fun invoke(): suspend () -> TonNodeBlockIdExt
}

@Refreshable
class FixedReferenceBlock(private val context: ApplicationContext) : ReferenceBlock {
    private lateinit var referenceBlock: TonNodeBlockIdExt

    override operator fun invoke() = suspend {
        if (!::referenceBlock.isInitialized) {
            referenceBlock = context.getBean(LiteApi::class.java).getMasterchainInfo().last
        }
        referenceBlock
    }
}

@Prototype
class LatestReferenceBlock(private val liteApi: LiteApi) : ReferenceBlock {
    override fun invoke() = suspend {
        liteApi.getMasterchainInfo().last
    }
}

