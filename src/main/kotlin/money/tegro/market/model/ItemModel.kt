package money.tegro.market.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.block.AddrNone
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("items")
data class ItemModel(
    @Id
    val address: MsgAddressInt,
    val initialized: Boolean = false,
    val index: Long = 0,
    val collection: MsgAddress = AddrNone,
    val owner: MsgAddress = AddrNone,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val attributes: Map<String, String>? = null,
    val updated: Instant = Instant.now()
)
