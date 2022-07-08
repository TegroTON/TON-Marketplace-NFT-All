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

@MappedEntity("items")
@Schema(hidden = true)
data class ItemModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    // Basic info
    val initialized: Boolean,

    val index: Long,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val collection: MsgAddress,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val owner: MsgAddress,


    // Metadata information
    val name: String? = null,

    val description: String? = null,

    val image: String? = null,

    val imageData: ByteArray = byteArrayOf(),


    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
    val metadataUpdated: Instant = Instant.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemModel

        if (address != other.address) return false
        if (initialized != other.initialized) return false
        if (index != other.index) return false
        if (collection != other.collection) return false
        if (owner != other.owner) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (discovered != other.discovered) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + initialized.hashCode()
        result = 31 * result + index.hashCode()
        result = 31 * result + collection.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + discovered.hashCode()
        return result
    }
}
