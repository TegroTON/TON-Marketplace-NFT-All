package money.tegro.market.controller

import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.model.TransactionRequestModel
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.SecureRandom
import org.ton.tlb.storeTlb
import kotlin.random.nextULong

@RestController
class APIv1Controller {
    @RequestMapping("/api/v1/transfer")
    suspend fun transfer(
        @RequestParam(required = true) item: MsgAddressInt,
        @RequestParam(required = true) newOwner: MsgAddressInt,
        @RequestParam responseDestination: MsgAddressInt?,
    ) = TransactionRequestModel(
        dest = item,
        value = BigInt(100_000_000),
        stateInit = null,
        text = "NFT Item Transfer",
        payload = CellBuilder.createCell {
            storeTlb(
                ItemOp, TransferOp(
                    query_id = SecureRandom.nextULong(),
                    new_owner = newOwner,
                    response_destination = responseDestination ?: AddrNone,
                    custom_payload = Maybe.of(null),
                    forward_amount = VarUInteger(BigInt(50_000_000)),
                    forward_payload = Either.of(Cell.of(), null)
                )
            )
        }
    )
}
