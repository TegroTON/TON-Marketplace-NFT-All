package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_metadata")
class ItemMetadata(
    @OneToOne
    @JoinColumn(name = "item", nullable = false)
    val item: ItemInfo,

    @Column(name = "name")
    val name: String? = null,
    @Column(name = "description")
    val description: String? = null,
    @Column(name = "image")
    val image: String? = null,
    @Column(name = "image_data")
    val imageData: ByteArray? = null,

    @OneToMany
    @JoinColumn(name = "attributes")
    val attributes: List<ItemAttribute>? = null,

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
