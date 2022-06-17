package money.tegro.market.db

import org.ton.block.MsgAddressIntStd
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collection")
    var collection: CollectionInfo? = null,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,
    @Column(name = "content")
    @Lob
    var content: ByteArray? = null,

    @OneToOne(cascade = [CascadeType.ALL], mappedBy = "item")
    var approval: ItemApproval? = null,
    @OneToOne(cascade = [CascadeType.ALL], mappedBy = "item")
    var royalty: ItemRoyalty? = null,
    @OneToOne(cascade = [CascadeType.ALL], mappedBy = "item")
    var metadata: ItemMetadata? = null,
    @OneToOne(cascade = [CascadeType.ALL], mappedBy = "item")
    var sale: ItemSale? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : UpdatableEntity, AddressableEntity() {
    constructor(address: MsgAddressIntStd) : this(address.workchainId, address.address.toByteArray())

    fun ownerAddressStd() = MsgAddressIntStd(workchain, address)
}
