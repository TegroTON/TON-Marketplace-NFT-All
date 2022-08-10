package money.tegro.market.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("sales")
data class SaleModel(
    @Id
    val address: MsgAddressInt,
    val marketplace: MsgAddress,
    val item: MsgAddress,
    val owner: MsgAddress,
    val fullPrice: BigInt,
    val marketplaceFee: BigInt,
    val royalty: BigInt,
    val royaltyDestination: MsgAddress,
    val updated: Instant = Instant.now(),
)
