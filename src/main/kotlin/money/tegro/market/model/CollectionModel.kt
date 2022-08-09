package money.tegro.market.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.converter.MsgAddressAttributeConverter
import money.tegro.market.core.converter.MsgAddressIntAttributeConverter
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("collections")
@Schema(hidden = true)
data class CollectionModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

    // Basic info
    val nextItemIndex: Long,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val owner: MsgAddress,

    // Metadata information
    val name: String? = null,

    val description: String? = null,

    val image: String? = null,

    val coverImage: String? = null,

    val enabled: Boolean = false,
    val updated: Instant = Instant.now(),
)
