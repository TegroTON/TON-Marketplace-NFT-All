package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_approvals")
class ItemApproval(
    @OneToOne
    @JoinColumn(name = "item", nullable = false)
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : UpdatableEntity
