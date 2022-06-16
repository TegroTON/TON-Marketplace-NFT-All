package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_metadata")
class ItemMetadata(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    val item: ItemInfo,

    @Column(name = "name")
    var name: String? = null,
    @Column(name = "description")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    var description: String? = null,
    @Column(name = "image")
    var image: String? = null,
    @Column(name = "image_data")
    var imageData: ByteArray? = null,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "metadata")
    var attributes: MutableSet<ItemAttribute>? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity {
    init {
        item.metadata = this
    }
}
