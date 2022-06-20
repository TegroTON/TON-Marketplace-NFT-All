package money.tegro.market.core.model

import io.micronaut.data.annotation.*
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@MappedEntity("ITEMS")
data class ItemData(
    val workchain: Int,
    val address: ByteArray,

    @Relation(Relation.Kind.MANY_TO_ONE)
    var collection: CollectionData? = null,

    // Basic info
    var initialized: Boolean = false,
    var index: Long? = null,
    var ownerWorkchain: Int? = null,
    var ownerAddress: ByteArray? = null,
    var content: ByteArray? = null,

    // Metadata information
    var name: String? = null,
    var description: String? = null,
    var image: String? = null,
    var imageData: ByteArray? = null,
//    @Relation(Relation.Kind.ONE_TO_MANY, mappedBy = "item")
//    val attributes: List<ItemAttribute>? = null,

    // Royalty information
    var numerator: Int? = null,
    var denominator: Int? = null,
    var destinationWorkchain: Int? = null,
    var destinationAddress: ByteArray? = null,

    val discovered: Instant = Instant.now(),
    @DateUpdated
    var updated: Instant? = null,
    var modified: Instant? = null,
) {
    @field:Id
    @GeneratedValue
    var id: Long? = null

    constructor(addressStd: MsgAddressIntStd) : this(addressStd.workchainId, addressStd.address.toByteArray())
}

