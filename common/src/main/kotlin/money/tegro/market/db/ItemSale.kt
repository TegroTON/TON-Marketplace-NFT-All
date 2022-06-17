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
    @Column(name = "workchain")
    var workchain: Int? = null,
    @Column(name = "address", unique = true, length = 32)
    var address: ByteArray? = null,
    @Column(name = "marketplace_workchain")
    var marketplaceWorkchain: Int? = null,
    @Column(name = "marketplace_address", length = 32)
    var marketplaceAddress: ByteArray? = null,
    @Column(name = "marketplace_fee")
    var marketplaceFee: Long? = null,
    @Column(name = "owner_workchain")
    var ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    var ownerAddress: ByteArray? = null,
    @Column(name = "price")
    var price: Long? = null,
    @Column(name = "royalty")
    var royalty: Long? = null,
    @Column(name = "royalty_destination_workchain")
    var royaltyDestinationWorkchain: Int? = null,
    @Column(name = "royalty_destination_address", length = 32)
    var royaltyDestinationAddress: ByteArray? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity
