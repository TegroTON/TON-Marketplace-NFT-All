package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "item_royalties")
class ItemRoyalty(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    val item: ItemInfo,

    @Column(name = "numerator")
    override var numerator: Int? = null,
    @Column(name = "denominator")
    override var denominator: Int? = null,
    @Column(name = "destination_workchain")
    override var destinationWorkchain: Int? = null,
    @Column(name = "destination_address", length = 32)
    override var destinationAddress: ByteArray? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : RoyaltyEntity, UpdatableEntity {
    init {
        item.royalty = this
    }
}