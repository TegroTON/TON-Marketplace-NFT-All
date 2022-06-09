package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ItemsTable : LongIdTable("items") {
    // basic properties, present in all items
    val workchain = integer("workchain")
    val address = binary("address", 32).uniqueIndex()
    val initialized = bool("initialized").default(false)

    // properties of initialized items
    val index = long("index").nullable()
    val collection = reference("collection", CollectionsTable).nullable()
    val ownerWorkchain = integer("owner_workchain").nullable()
    val ownerAddress = binary("owner_address", 32).nullable()

    // royalty-related properties
    val royaltyNumerator = integer("royalty_numerator").nullable()
    val royaltyDenominator = integer("royalty_denominator").nullable()
    val royaltyDestinationWorkchain = integer("royalty_destination_workchain").nullable()
    val royaltyDestinationAddress = binary("royalty_destination_address").nullable()

    // items that are owned by the NFTSale contracts have the following fields
    val marketplaceWorkchain = integer("marketplace_workchain").nullable()
    val marketplaceAddress = binary("marketplace_address", 32).nullable()
    val sellerWorkchain = integer("seller_workchain").nullable()
    val sellerAddress = binary("seller_address", 32).nullable()
    val price = long("price").nullable()
    val marketplaceFee = long("marketplace_fee").nullable()

    // NOT THE SAME AS `royaltyDestination*` - a rogue seller contract might send royalties to a different address
    // This shows the actual address where `saleRoyalty` will be sent
    val saleRoyaltyDestinationWorkchain = integer("sale_royalty_destination_workchain").nullable()
    val saleRoyaltyDestinationAddress = binary("sale_royalty_destination_address").nullable()
    val saleRoyalty = long("sale_royalty").nullable()

    // metadata-related properties
    val name = text("name").nullable()
    val description = text("description").nullable()
    val image = text("image").nullable()
    val imageData = blob("image_data").nullable()

    // various internal properties
    val approved = bool("approved").nullable()

    /** First time this particular item was added to the database */
    val discovered = timestamp("discovered")

    /** Last time item was indexed by the nightcrawler tool */
    val dataLastIndexed = timestamp("data_last_indexed").nullable()
    val royaltyLastIndexed = timestamp("royalty_last_indexed").nullable()
    val ownerLastIndexed = timestamp("owner_last_indexed").nullable()
    val metadataLastIndexed = timestamp("metadata_last_indexed").nullable()
}
