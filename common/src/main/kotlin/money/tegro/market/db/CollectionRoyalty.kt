package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection_royalties")
class CollectionRoyalty(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    val collection: CollectionInfo,

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
        collection.royalty = this
    }
}
