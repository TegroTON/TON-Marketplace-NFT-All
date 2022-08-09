package money.tegro.market.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.converter.MsgAddressIntAttributeConverter
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("accounts")
@Schema(hidden = true)
data class AccountModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

    val updated: Instant = Instant.now(),
)
