package money.tegro.market.core.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.block.AddrNone
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("royalties")
data class RoyaltyModel(
    @Id
    val address: MsgAddressInt,
    val numerator: Int = 0,
    val denominator: Int = 0,
    val destination: MsgAddress = AddrNone,
    val updated: Instant = Instant.now(),
)
