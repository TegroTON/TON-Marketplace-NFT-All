package money.tegro.market.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("approvals")
data class ApprovalModel(
    @Id
    val address: MsgAddressInt,
    val approved: Boolean = false,
    val timestamp: Instant = Instant.now(),
)
