package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CollectionsTable : LongIdTable("collections") {
    // basic collection properties, present in all kinds of collections
    val workchain = integer("workchain")
    val address = binary("address", 32).uniqueIndex()
    val ownerWorkchain = integer("owner_workchain")
    val ownerAddress = binary("owner_address", 32)
    val nextItemIndex = long("next_item_index")

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

    val coverImageUrl = text("metadata_cover_image_url").nullable()
    val coverImageIpfs = text("metadata_cover_image_ipfs").nullable()
    val coverImageData = blob("metadata_cover_image_data").nullable()

    // various internal properties
    /** If true, collection was manually approved and is public */
    val approved = bool("approved").default(false)

    /** First time this particular collection was added to the database */
    val discovered = timestamp("discovered")

    /** Last time collection was indexed by the nightcrawler tool */
    val lastIndexed = timestamp("last_indexed")
}
