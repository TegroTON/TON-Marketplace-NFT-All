package money.tegro.market.db

import javax.persistence.*

@Entity
@Table(name = "item_attributes")
class ItemAttribute(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "metadata", nullable = false)
    val metadata: ItemMetadata,

    @Column(name = "trait", nullable = false)
    val trait: String,
    @Column(name = "value_", nullable = false)
    val value: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
