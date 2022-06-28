package money.tegro.market.core.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTMetadataAttribute
import money.tegro.market.core.key.AddressKey

@MappedEntity("ATTRIBUTES")
@Schema(hidden = true)
data class AttributeModel(
    @Relation(Relation.Kind.EMBEDDED)
    val item: AddressKey,

    var trait: String,
    var value: String,

    @field:Id
    @field:GeneratedValue
    var id: Long? = null
) {
    constructor(item: AddressKey, attribute: NFTMetadataAttribute) : this(
        item,
        attribute.trait,
        attribute.value
    )
}

