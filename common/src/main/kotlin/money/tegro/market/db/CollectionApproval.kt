package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection_approvals")
class CollectionApproval(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    val collection: CollectionInfo,

    @Column(name = "approved")
    val approved: Boolean = false,

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
        collection.approval = this
    }
}
