package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_sales")
class ItemSale(
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    val item: ItemInfo,
    @Column(name = "workchain", nullable = false)
    override val workchain: Int,
    @Column(name = "address", nullable = false, unique = true, length = 32)
    override val address: ByteArray,
    @Column(name = "marketplace_workchain", nullable = false)
    val marketplaceWorkchain: Int,
    @Column(name = "marketplace_address", nullable = false, length = 32)
    val marketplaceAddress: ByteArray,
    @Column(name = "marketplace_fee")
    val marketplaceFee: Long? = null,
    @Column(name = "owner_workchain", nullable = false)
    val ownerWorkchain: Int,
    @Column(name = "owner_address", nullable = false, length = 32)
    val ownerAddress: ByteArray,
    @Column(name = "price", nullable = false)
    val price: Long,
    @Column(name = "royalty")
    val royalty: Long? = null,
    @Column(name = "royalty_destination_workchain")
    val royaltyDestinationWorkchain: Int? = null,
    @Column(name = "royalty_destination_address", length = 32)
    val royaltyDestinationAddress: ByteArray,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity, AddressableEntity()
