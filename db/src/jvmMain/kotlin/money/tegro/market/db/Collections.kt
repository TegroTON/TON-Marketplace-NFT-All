package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable

object Collections : LongIdTable("nft_collections") {
    val workchain = integer("workchain")
    val address = binary("address", 32).uniqueIndex()
    val ownerWorkchain = integer("owner_workchain")
    val ownerAddress = binary("owner_address", 32)
    val size = long("size")

    //    val itemCode = binary("item_code")

    val royaltyNumerator = integer("royalty_numerator").nullable()
    val royaltyDenominator = integer("royalty_denominator").nullable()
    val royaltyDestinationWorkchain = integer("royalty_destination_workchain").nullable()
    val royaltyDestinationAddress = binary("royalty_destination_address").nullable()
}
