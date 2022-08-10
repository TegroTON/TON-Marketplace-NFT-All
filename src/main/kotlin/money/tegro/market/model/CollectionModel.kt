package money.tegro.market.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.ton.block.AddrNone
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@Table("collections")
data class CollectionModel(
    @Id
    val address: MsgAddressInt,
    val nextItemIndex: Long = 0L,
    val owner: MsgAddress = AddrNone,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val coverImage: String? = null,
    val updated: Instant = Instant.now()
)
