package money.tegro.market.core.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.converter.AddrStdAttributeConverter
import money.tegro.market.core.converter.MsgAddressAttributeConverter
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import java.time.Instant

@MappedEntity("collections")
@Schema(hidden = true)
data class CollectionModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    // Basic info
    val nextItemIndex: Long,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val owner: MsgAddress,

    // Metadata information
    val name: String = "",

    val description: String = "",

    val image: String? = null,

    val imageData: ByteArray = byteArrayOf(),

    val coverImage: String? = null,

    val coverImageData: ByteArray = byteArrayOf(),


    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
    val metadataUpdated: Instant = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionModel

        if (address != other.address) return false
        if (nextItemIndex != other.nextItemIndex) return false
        if (owner != other.owner) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (coverImage != other.coverImage) return false
        if (!coverImageData.contentEquals(other.coverImageData)) return false
        if (discovered != other.discovered) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + nextItemIndex.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + (coverImage?.hashCode() ?: 0)
        result = 31 * result + coverImageData.contentHashCode()
        result = 31 * result + discovered.hashCode()
        return result
    }
}
