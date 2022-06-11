package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

//@Entity
//@Table(name = "items")
class ItemEntity(
    // Basic item properties
    @Column(name = "workchain", nullable = false)
    val workchain: Int,
    @Column(name = "address", nullable = false, unique = true, length = 32)
    val address: ByteArray,
    @Column(name = "initialized", nullable = false)
    val initialized: Boolean = false,

    @Column(name = "index")
    val index: Long? = null,
    @ManyToOne
    @JoinColumn(name = "collection", referencedColumnName = "id")
    val collection: CollectionInfo? = null,
    @Column(name = "owner_workchain")
    val ownerWorkchain: Int? = null,
    @Column(name = "owner_address", length = 32)
    val ownerAddress: ByteArray? = null,

    // Royalty-related properties
    @Column(name = "royalty_numerator")
    override val royaltyNumerator: Int? = null,
    @Column(name = "royalty_denominator")
    override val royaltyDenominator: Int? = null,
    @Column(name = "royalty_destination_workchain")
    override val royaltyDestinationWorkchain: Int? = null,
    @Column(name = "royalty_destination_address", length = 32)
    override val royaltyDestinationAddress: ByteArray? = null,

    // Metadata-related properties
    @Column(name = "name")
    val name: String? = null,
    @Column(name = "description")
    val description: String? = null,
    @Column(name = "image")
    val image: String? = null,
    @Column(name = "image_data")
    val imageData: ByteArray? = null,
    @OneToMany
    @JoinColumn(name = "attributes", referencedColumnName = "id")
    val attributes: List<ItemAttributeEntity>? = null,

    // Internal properties
    @Column(name = "approved", nullable = false)
    val approved: Boolean = false,
    @Column(name = "discovered", nullable = false)
    val discovered: Instant = Instant.now(),
    @Column(name = "data_last_indexed")
    val dataLastIndexed: Instant? = null,
    @Column(name = "royalty_last_indexed")
    override val royaltyLastIndexed: Instant? = null,
    @Column(name = "metadata_last_indexed")
    val metadataLastIndexed: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) : Royalty
