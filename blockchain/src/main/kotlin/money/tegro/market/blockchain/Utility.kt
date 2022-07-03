package money.tegro.market.blockchain

import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi

fun LiteApi.referenceBlock(): suspend () -> TonNodeBlockIdExt = { this.getMasterchainInfo().last }
