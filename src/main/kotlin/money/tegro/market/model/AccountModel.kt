package money.tegro.market.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("accounts")
data class AccountModel(
    @Id
    val address: MsgAddressInt,

    val updated: Instant = Instant.now()
)
