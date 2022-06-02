package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressInt

object Items : LongIdTable("nft_items") {
    val workchain = integer("workchain")
    val address = binary("address", 32).uniqueIndex()
    val initialized = bool("initialized")
    val index = long("index").nullable()
    val collection = reference("collection", Collections).nullable()
    val ownerWorkchain = integer("owner_workchain").nullable()
    val ownerAddress = binary("owner_address", 32).nullable()

    fun find(collection: MsgAddressInt.AddrStd) =
        (this.workchain eq collection.workchainId) and (this.address eq collection.address)
}
