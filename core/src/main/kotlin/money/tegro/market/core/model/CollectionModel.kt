package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("COLLECTIONS")
@Schema(hidden = true)
data class CollectionModel(
    @EmbeddedId
    val address: AddressKey,

    // Basic info
    var nextItemIndex: Long? = null,
    var content: ByteArray? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var owner: AddressKey? = null,

    val discovered: Instant = Instant.now(),
    var updated: Instant = Instant.MIN,

    // Metadata information
    var name: String? = null,
    var description: String? = null,
    var image: String? = null,
    var imageData: ByteArray? = null,
    var coverImage: String? = null,
    var coverImageData: ByteArray? = null,

    var metadataUpdated: Instant = Instant.MIN,
) {
    constructor(address: AddrStd) : this(AddressKey.of(address))
}

