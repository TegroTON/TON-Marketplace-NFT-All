package money.tegro.market.core.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema

@MappedEntity("ITEM_ATTRIBUTES")
@Schema(hidden = true)
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

