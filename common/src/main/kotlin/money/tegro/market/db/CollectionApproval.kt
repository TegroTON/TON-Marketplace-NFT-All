package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection_approvals")
class CollectionApproval(
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    val collection: CollectionInfo,

    @Column(name = "approved")
    val approved: Boolean = false,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity
