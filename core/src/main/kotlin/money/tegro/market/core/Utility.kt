package money.tegro.market.core.dto

import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import org.ton.block.MsgAddress

fun AddrStd.toSafeBounceable() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)

fun AddressKey.toSafeBounceable() = this.to().toSafeBounceable()

fun MsgAddress.toSafeBounceable() = (this as? AddrStd)?.toSafeBounceable()

fun AddrStd.toKey() = AddressKey.of(this)

fun MsgAddress.toKey() = AddressKey.of(this)
