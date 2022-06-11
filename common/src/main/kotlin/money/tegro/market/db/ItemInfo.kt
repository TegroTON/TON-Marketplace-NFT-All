package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "items")
class ItemInfo(
    @Column(name = "workchain", nullable = false)
    override val workchain: Int,
    @Column(name = "address", nullable = false, unique = true, length = 32)
    override val address: ByteArray,
    @Column(name = "initialized", nullable = false)
    var initialized: Boolean = false,
    @Column(name = "index")
    var index: Long? = null,
    @ManyToOne
    @JoinColumn(name = "collection")
    val collection: CollectionInfo? = null,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,

    @OneToOne
    @JoinColumn(name = "approval")
    val approval: ItemApproval? = null,
    @OneToOne
    @JoinColumn(name = "royalty")
    val royalty: ItemRoyalty? = null,
    @OneToOne
    @JoinColumn(name = "metadata")
    val metadata: ItemMetadata? = null,
    @OneToOne
    @JoinColumn(name = "sale")
    val sale: ItemSale? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : UpdatableEntity, AddressableEntity()
