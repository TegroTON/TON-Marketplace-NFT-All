package money.tegro.market.core.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.converter.AddrStdAttributeConverter
import org.ton.block.AddrStd

@MappedEntity("attributes")
@Schema(hidden = true)
data class AttributeModel(
    /** Item that this attribute applies to */
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val item: AddrStd,

    val trait: String,

    val value: String,

    @field:Id
    @field:GeneratedValue
    var id: Long? = null
)
