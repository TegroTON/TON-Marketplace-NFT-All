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
)
