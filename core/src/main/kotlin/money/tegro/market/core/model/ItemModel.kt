package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.key.RoyaltyKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("ITEMS")
@Schema(hidden = true)
data class ItemModel(
    @EmbeddedId
    override val address: AddressKey,

    // Basic info
    var initialized: Boolean = false,
    var index: Long? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var collection: AddressKey? = null,
    @Relation(Relation.Kind.EMBEDDED)
    var owner: AddressKey? = null,
    override var content: ByteArray? = null,

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
    constructor(address: AddrStd) : this(AddressKey.of(address))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemModel

        if (address != other.address) return false
        if (initialized != other.initialized) return false
        if (index != other.index) return false
        if (collection != other.collection) return false
        if (owner != other.owner) return false
        if (content != null) {
            if (other.content == null) return false
            if (!content.contentEquals(other.content)) return false
        } else if (other.content != null) return false
        if (discovered != other.discovered) return false
        if (updated != other.updated) return false
        if (modified != other.modified) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false
        if (coverImage != other.coverImage) return false
        if (coverImageData != null) {
            if (other.coverImageData == null) return false
            if (!coverImageData.contentEquals(other.coverImageData)) return false
        } else if (other.coverImageData != null) return false
        if (metadataUpdated != other.metadataUpdated) return false
        if (metadataModified != other.metadataModified) return false
        if (royalty != other.royalty) return false
        if (royaltyUpdated != other.royaltyUpdated) return false
        if (royaltyModified != other.royaltyModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + initialized.hashCode()
        result = 31 * result + (index?.hashCode() ?: 0)
        result = 31 * result + (collection?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)
        result = 31 * result + (content?.contentHashCode() ?: 0)
        result = 31 * result + discovered.hashCode()
        result = 31 * result + updated.hashCode()
        result = 31 * result + modified.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        result = 31 * result + (coverImage?.hashCode() ?: 0)
        result = 31 * result + (coverImageData?.contentHashCode() ?: 0)
        result = 31 * result + metadataUpdated.hashCode()
        result = 31 * result + metadataModified.hashCode()
        result = 31 * result + (royalty?.hashCode() ?: 0)
        result = 31 * result + royaltyUpdated.hashCode()
        result = 31 * result + royaltyModified.hashCode()
        return result
    }
}

