package money.tegro.market.model

import money.tegro.market.toBase64
import org.ton.block.AddrStd
import org.ton.block.Coins
import org.ton.block.MsgAddressInt
import org.ton.cell.Cell

data class TransactionRequestModel(
    val dest: String,
    val value: String,
    val stateInit: String?,
    val text: String?,
    val payload: String?,
) {
    constructor(dest: MsgAddressInt, value: Coins, stateInit: Cell?, text: String?, payload: Cell?) : this(
        dest = (dest as AddrStd).toString(userFriendly = true, urlSafe = true),
        value = value.amount.value.toString(),
        stateInit = stateInit?.toBase64(),
        text = text,
        payload = payload?.toBase64(),
    )
}
