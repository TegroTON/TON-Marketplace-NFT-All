package money.tegro.market.db

import org.ton.block.MsgAddressIntStd
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collections")
class CollectionInfo(
    @Column(name = "workchain", nullable = false)
    override val workchain: Int,
    @Column(name = "address", nullable = false, unique = true, length = 32)
    override val address: ByteArray,
    @Column(name = "next_item_index")
    var nextItemIndex: Long? = null,
    @Column(name = "content")
    @Lob
    var content: ByteArray? = null,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "approval")
    var approval: CollectionApproval? = null,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "royalty")
    var royalty: CollectionRoyalty? = null,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "metadata")
    var metadata: CollectionMetadata? = null,

    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "collection")
    var items: MutableSet<ItemInfo>? = null,

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
    fun address() = MsgAddressIntStd(workchain, address)

    fun owner() =
        ownerWorkchain?.let { workchain -> ownerAddress?.let { address -> MsgAddressIntStd(workchain, address) } }

    fun owner(address: MsgAddressIntStd) {
        ownerWorkchain = address.workchainId
        ownerAddress = address.address.toByteArray()
    }
}