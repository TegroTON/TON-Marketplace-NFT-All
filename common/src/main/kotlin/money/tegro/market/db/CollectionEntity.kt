package money.tegro.market.db

import org.hibernate.internal.util.SerializationHelper
import org.ton.block.MsgAddressIntStd
import java.io.Serializable
import java.time.Instant
import javax.persistence.*


@Entity
@Table(name = "collections")
class CollectionInfo(
    @Column(name = "workchain", nullable = false)
    val workchain: Int,
    @Column(name = "address", nullable = false, unique = true, length = 32)
    val address: ByteArray,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,
    @Column(name = "next_item_index")
    var nextItemIndex: Long? = null,

    @OneToOne
    @JoinColumn(name = "approval")
    val approval: CollectionApproval? = null,
    @OneToOne
    @JoinColumn(name = "royalty")
    val royalty: CollectionRoyalty? = null,
    @OneToOne
    @JoinColumn(name = "metadata")
    val metadata: CollectionMetadata? = null,
//    @OneToMany
//    @JoinColumn(name = "items")
//    val items: List<ItemEntity>? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : Serializable, UpdatableEntity {
    fun address() = MsgAddressIntStd(workchain, address)

    fun owner() =
        ownerWorkchain?.let { workchain -> ownerAddress?.let { address -> MsgAddressIntStd(workchain, address) } }

    fun owner(address: MsgAddressIntStd) {
        ownerWorkchain = address.workchainId
        ownerAddress = address.address.toByteArray()
    }

    fun clone() = SerializationHelper.clone(this as Serializable)
}

@Entity
@Table(name = "collection_royalties")
class CollectionRoyalty(
    @OneToOne
    @JoinColumn(name = "collection", nullable = false)
    val collection: CollectionInfo,

    @Column(name = "royalty_numerator")
    override val royaltyNumerator: Int? = null,
    @Column(name = "royalty_denominator")
    override val royaltyDenominator: Int? = null,
    @Column(name = "royalty_destination_workchain")
    override val royaltyDestinationWorkchain: Int? = null,
    @Column(name = "royalty_destination_address", length = 32)
    override val royaltyDestinationAddress: ByteArray? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override val updated: Instant? = null,
    @Column(name = "modified")
    override val modified: Instant? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : RoyaltyEntity, UpdatableEntity

@Entity
@Table(name = "collection_metadata")
class CollectionMetadata(
    @OneToOne
    @JoinColumn(name = "collection", nullable = false)
    val collection: CollectionInfo,

    @Column(name = "name")
    val name: String? = null,
    @Column(name = "description")
    val description: String? = null,
    @Column(name = "image")
    val image: String? = null,
    @Column(name = "image_data")
    val imageData: ByteArray? = null,
    @Column(name = "cover_image")
    val coverImage: String? = null,
    @Column(name = "cover_image_data")
    val coverImageData: ByteArray? = null,

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


@Entity
@Table(name = "collection_approvals")
class CollectionApproval(
    @OneToOne
    @JoinColumn(name = "collection", nullable = false)
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : UpdatableEntity
