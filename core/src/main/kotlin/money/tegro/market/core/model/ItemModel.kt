package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("ITEMS")
@Schema(hidden = true)
data class ItemModel(
    @EmbeddedId
    val address: AddressKey,

    // Basic info
    var initialized: Boolean = false,
    var index: Long? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var collection: AddressKey? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var owner: AddressKey? = null,
    var content: ByteArray? = null,

    val discovered: Instant = Instant.now(),
    var updated: Instant = Instant.MIN,

    // Metadata information
    var name: String? = null,
    var description: String? = null,
    var image: String? = null,
    var imageData: ByteArray? = null,

    var metadataUpdated: Instant = Instant.MIN,
) {
    constructor(address: AddrStd) : this(AddressKey.of(address))
}

