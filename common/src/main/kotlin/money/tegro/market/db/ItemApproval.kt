package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_approvals")
class ItemApproval(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    val item: ItemInfo,

    @Column(name = "approved")
    val approved: Boolean? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override val updated: Instant? = null,
    @Column(name = "modified")
    override val modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity {
    init {
        item.approval = this
    }
}
