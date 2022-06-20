package money.tegro.market.core.model

import io.micronaut.data.annotation.*
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@MappedEntity("COLLECTIONS")
data class CollectionData(
    val workchain: Int,
    val address: ByteArray,

    @Relation(Relation.Kind.ONE_TO_MANY, mappedBy = "collection")
    val items: List<ItemData>? = null,

    // Basic info
    var nextItemIndex: Long? = null,
    var content: ByteArray? = null,
    var ownerWorkchain: Int? = null,
    var ownerAddress: ByteArray? = null,

    // Metadata information
    var name: String? = null,
    var description: String? = null,
    var image: String? = null,
    var imageData: ByteArray? = null,
    var coverImage: String? = null,
    var coverImageData: ByteArray? = null,

    // Royalty information
    var numerator: Int? = null,
    var denominator: Int? = null,
    var destinationWorkchain: Int? = null,
    var destinationAddress: ByteArray? = null,

    val discovered: Instant = Instant.now(),
    @DateUpdated
    var updated: Instant? = null,
    var modified: Instant? = null,

    @field:Id
    @GeneratedValue
    var id: Long? = null
) {
    constructor(addressStd: MsgAddressIntStd) : this(addressStd.workchainId, addressStd.address.toByteArray())

    fun addressStd() = MsgAddressIntStd(workchain, address)
}

