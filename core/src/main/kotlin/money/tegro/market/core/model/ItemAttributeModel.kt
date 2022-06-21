package money.tegro.market.core.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation

@MappedEntity("ITEM_ATTRIBUTES")
data class ItemAttributeModel(
    @Relation(Relation.Kind.MANY_TO_ONE)
    val item: ItemModel,

    var trait: String,
    var value: String,

    @field:Id
    @field:GeneratedValue
    var id: Long? = null
) {
}

