package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable

object Items : LongIdTable("nft_items") {
    val workchain = integer("workchain").uniqueIndex()
    val address = binary("address", 32).uniqueIndex()
    val initialized = bool("initialized")
    val index = long("index").nullable()
    val collection = reference("collection", Collections).nullable()
    val ownerWorkchain = integer("owner_workchain").nullable()
    val ownerAddress = binary("owner_address", 32).nullable()
}
