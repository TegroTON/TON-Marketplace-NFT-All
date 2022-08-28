package money.tegro.market.query

import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.op.ItemOp
import money.tegro.market.op.TransferItemOp
import money.tegro.market.service.CollectionService
import org.springframework.stereotype.Component
import org.ton.bigint.BigInt
import org.ton.block.Either
import org.ton.block.Maybe
import org.ton.block.MsgAddressInt
import org.ton.block.VarUInteger
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.SecureRandom
import org.ton.tlb.storeTlb
import kotlin.random.nextULong

@Component
class RootQuery(
    private val collectionService: CollectionService,
) : Query {
    suspend fun collections(
        drop: Int? = null,
        take: Int? = null,
    ) =
        collectionService.listAll()
            .dropTake(drop, take)
            .map { CollectionQuery(it) }
            .toList()

    suspend fun collection(address: String) =
        CollectionQuery(MsgAddressInt(address))

    suspend fun item(address: String) =
        ItemQuery(MsgAddressInt(address))

    suspend fun transfer(item: String, destination: String, response: String) = TransactionRequestQuery(
        dest = MsgAddressInt(item),
        value = BigInt(100_000_000),
        stateInit = null,
        payload = CellBuilder.createCell {
            storeTlb(
                ItemOp, TransferItemOp(
                    query_id = SecureRandom.nextULong(),
                    new_owner = MsgAddressInt(destination),
                    response_destination = MsgAddressInt(response),
                    custom_payload = Maybe.of(null),
                    forward_amount = VarUInteger(BigInt.ZERO),
                    forward_payload = Either.of(Cell.of(), null)
                )
            )
        }
    )
}
