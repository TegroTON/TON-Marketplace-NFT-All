package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.toBase64
import money.tegro.market.toRaw
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import org.ton.cell.Cell

@GraphQLName("TransactionRequest")
data class TransactionRequestQuery(
    val dest: String,
    val value: String,
    val stateInit: String?,
    val payload: String?,
) {
    constructor(dest: MsgAddressInt, value: BigInt, stateInit: Cell?, payload: Cell?) : this(
        dest = dest.toRaw(),
        value = value.toString(),
        stateInit = stateInit?.toBase64(),
        payload = payload?.toBase64(),
    )
}
