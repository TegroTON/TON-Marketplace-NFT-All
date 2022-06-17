package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection_metadata")
class CollectionMetadata(
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "id")
    @MapsId
    val collection: CollectionInfo,

    @Column(name = "name")
    var name: String? = null,
    @Column(name = "description")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    var description: String? = null,
    @Column(name = "image")
    var image: String? = null,
    @Column(name = "image_data")
    var imageData: ByteArray? = null,
    @Column(name = "cover_image")
    var coverImage: String? = null,
    @Column(name = "cover_image_data")
    var coverImageData: ByteArray? = null,

    @Column(name = "discovered", nullable = false)
    override val discovered: Instant = Instant.now(),
    @Column(name = "updated")
    override var updated: Instant? = null,
    @Column(name = "modified")
    override var modified: Instant? = null,
    @Id
    var id: Long? = null,
) : UpdatableEntity
