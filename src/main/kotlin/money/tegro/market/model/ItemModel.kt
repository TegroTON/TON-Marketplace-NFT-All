package money.tegro.market.core.model

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

@MappedEntity("items")
@Schema(hidden = true)
data class ItemModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

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

    @field:TypeDef(type = DataType.JSON)
    val attributes: Map<String, String> = mapOf(),

    val approved: Boolean = false,

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
)
