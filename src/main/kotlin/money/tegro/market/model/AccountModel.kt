package money.tegro.market.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.converter.AddrStdAttributeConverter
import money.tegro.market.core.converter.MsgAddressAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import java.time.Instant

@MappedEntity("accounts")
@Schema(hidden = true)
data class AccountModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    val kind: AccountKind,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val marketplace: MsgAddress = AddrNone,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val item: MsgAddress = AddrNone,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val owner: MsgAddress = AddrNone,

    val fullPrice: BigInt = BigInt(0),

    val marketplaceFee: BigInt = BigInt(0),

    val royalty: BigInt = BigInt(0),

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val royaltyDestination: MsgAddress = AddrNone,

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
)

enum class AccountKind {
    USER,
    SALE,
}
