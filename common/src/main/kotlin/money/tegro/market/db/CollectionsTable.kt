package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object CollectionsTable : LongIdTable("collections") {
    // basic collection properties, present in all kinds of collections
    val workchain = integer("workchain")
    val address = binary("address", 32).uniqueIndex()
    val ownerWorkchain = integer("owner_workchain").nullable()
    val ownerAddress = binary("owner_address", 32).nullable()
    val nextItemIndex = long("next_item_index").nullable()

    // royalty-related properties
    val royaltyNumerator = integer("royalty_numerator").nullable()
    val royaltyDenominator = integer("royalty_denominator").nullable()
    val royaltyDestinationWorkchain = integer("royalty_destination_workchain").nullable()
    val royaltyDestinationAddress = binary("royalty_destination_address").nullable()

    // metadata-related properties
    val name = text("name").nullable()
    val description = text("description").nullable()
    val image = text("image").nullable()
    val imageData = blob("image_data").nullable()
    val coverImage = text("cover_image").nullable()
    val coverImageData = blob("cover_image_data").nullable()

    // various internal properties
    /** If true, collection was manually approved and is public */
    val approved = bool("approved").default(false)

    /** First time this particular collection was added to the database */
    val discovered = timestamp("discovered")

    /** Last time collection was indexed by the nightcrawler tool */
    val dataLastIndexed = timestamp("data_last_indexed").nullable()
    val royaltyLastIndexed = timestamp("royalty_last_indexed").nullable()
    val metadataLastIndexed = timestamp("metadata_last_indexed").nullable()
}
