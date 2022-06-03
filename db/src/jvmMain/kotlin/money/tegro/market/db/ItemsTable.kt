package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

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

    // metadata-related properties
    val metadataUrl = text("metadata_url").nullable()
    val metadataIpfs = text("metadata_ipfs").nullable()

    val name = text("metadata_name")
    val description = text("metadata_description")

    val imageUrl = text("metadata_image_url").nullable()
    val imageIpfs = text("metadata_image_ipfs").nullable()
    val imageData = blob("metadata_image_data").nullable()

    // various internal properties
    val approved = bool("approved").nullable()

    /** First time this particular collection was added to the database */
    val discovered = timestamp("discovered")

    /** Last time collection was indexed by the nightcrawler tool */
    val lastIndexed = timestamp("last_indexed")
}
