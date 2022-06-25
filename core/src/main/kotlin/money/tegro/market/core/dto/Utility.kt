package money.tegro.market.core.dto

import org.ton.block.AddrStd

fun AddrStd.toSafeBounceable() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)
