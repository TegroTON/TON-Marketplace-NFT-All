package money.tegro.market.core

import org.ton.block.AddrStd
import org.ton.block.MsgAddress

fun AddrStd.toSafeBounceable() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)

fun MsgAddress.toSafeBounceable() = (this as? AddrStd)?.toSafeBounceable()
