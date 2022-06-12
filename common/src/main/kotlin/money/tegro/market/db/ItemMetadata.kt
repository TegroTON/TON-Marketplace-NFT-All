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
    val name: String? = null,
    @Column(name = "description")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    val description: String? = null,
    @Column(name = "image")
    val image: String? = null,
    @Column(name = "image_data")
    val imageData: ByteArray? = null,

    @OneToMany
    @JoinColumn(name = "attributes")
    var attributes: MutableSet<ItemAttribute>? = null,

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
        item.metadata = this
    }
}
