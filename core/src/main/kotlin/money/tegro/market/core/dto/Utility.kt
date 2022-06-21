package money.tegro.market.core.dto

import org.ton.block.MsgAddressIntStd

fun MsgAddressIntStd.toSafeBounceable() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)
