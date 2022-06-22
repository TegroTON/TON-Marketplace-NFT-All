package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.key.RoyaltyKey
import org.ton.block.MsgAddressIntStd
import java.time.Instant

@MappedEntity("COLLECTIONS")
@Schema(hidden = true)
data class CollectionModel(
    @EmbeddedId
    override val address: AddressKey,

    // Basic info
    var nextItemIndex: Long? = null,
    override var content: ByteArray? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var owner: AddressKey? = null,

    override val discovered: Instant = Instant.now(),
    override var updated: Instant = Instant.MIN,
    override var modified: Instant = Instant.MIN,

    // Metadata information
    override var name: String? = null,
    override var description: String? = null,
    override var image: String? = null,
    override var imageData: ByteArray? = null,
    override var coverImage: String? = null,
    override var coverImageData: ByteArray? = null,

    override var metadataUpdated: Instant = Instant.MIN,
    override var metadataModified: Instant = Instant.MIN,

    // Royalty information
    @Relation(Relation.Kind.EMBEDDED)
    override var royalty: RoyaltyKey? = null,

    override var royaltyUpdated: Instant = Instant.MIN,
    override var royaltyModified: Instant = Instant.MIN,
) : BasicModel, MetadataModel, RoyaltyModel {
    constructor(address: MsgAddressIntStd) : this(AddressKey.of(address))
}

