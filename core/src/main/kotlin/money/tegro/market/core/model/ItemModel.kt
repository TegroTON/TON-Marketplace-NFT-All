package money.tegro.market.core.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@MappedEntity("ITEMS")
data class ItemModel(
    override val workchain: Int,
    override val address: ByteArray,

    @Relation(Relation.Kind.MANY_TO_ONE)
    var collection: CollectionModel? = null,

    // Basic info
    var initialized: Boolean = false,
    var index: Long? = null,
    var ownerWorkchain: Int? = null,
    var ownerAddress: ByteArray? = null,
    override var content: ByteArray? = null,

    override var dataUpdated: Instant? = null,
    override var dataModified: Instant? = null,

    override val discovered: Instant = Instant.now(),

    // Metadata information
    override var name: String? = null,
    override var description: String? = null,
    override var image: String? = null,
    override var imageData: ByteArray? = null,
    override var coverImage: String? = null,
    override var coverImageData: ByteArray? = null,

    @Relation(Relation.Kind.ONE_TO_MANY, mappedBy = "item")
    val attributes: List<ItemAttribute>? = null,

    override var metadataUpdated: Instant? = null,
    override var metadataModified: Instant? = null,

    // Royalty information
    override var numerator: Int? = null,
    override var denominator: Int? = null,
    override var destinationWorkchain: Int? = null,
    override var destinationAddress: ByteArray? = null,

    override var royaltyUpdated: Instant? = null,
    override var royaltyModified: Instant? = null,

    @field:Id
    @field:GeneratedValue
    override var id: Long? = null,
) : BasicModel, MetadataModel, RoyaltyModel {
    constructor(addressStd: MsgAddressIntStd) : this(addressStd.workchainId, addressStd.address.toByteArray())
}

