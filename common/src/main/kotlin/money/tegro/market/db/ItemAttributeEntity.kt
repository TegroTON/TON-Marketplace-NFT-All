package money.tegro.market.db

import javax.persistence.*

//@Entity
//@Table(name = "item_attributes")
class ItemAttributeEntity(
    @ManyToOne
    @JoinColumn(name = "item", referencedColumnName = "id", nullable = false)
    val item: ItemEntity,
    @Column(name = "trait", nullable = false)
    val trait: String,
    @Column(name = "value", nullable = false)
    val value: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
