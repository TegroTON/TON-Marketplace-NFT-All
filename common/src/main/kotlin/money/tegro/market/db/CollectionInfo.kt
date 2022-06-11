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
    var content: ByteArray? = null,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,

    @OneToOne
    @JoinColumn(name = "approval")
    val approval: CollectionApproval? = null,
    @OneToOne
    @JoinColumn(name = "royalty")
    val royalty: CollectionRoyalty? = null,
    @OneToOne
    @JoinColumn(name = "metadata")
    val metadata: CollectionMetadata? = null,
    @OneToMany
    @JoinColumn(name = "items")
    val items: List<ItemInfo>? = null,

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
