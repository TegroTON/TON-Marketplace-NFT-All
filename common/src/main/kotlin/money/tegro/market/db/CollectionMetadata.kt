package money.tegro.market.db

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection_metadata")
class CollectionMetadata(
    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
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
    var id: Long? = null,
) : UpdatableEntity {
    init {
        collection.metadata = this
    }
}
