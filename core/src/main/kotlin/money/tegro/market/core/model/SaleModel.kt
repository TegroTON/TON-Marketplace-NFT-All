package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import java.time.Instant

@MappedEntity("SALES")
@Schema(hidden = true)
data class SaleModel(
    @EmbeddedId
    override val address: AddressKey,

    @Relation(Relation.Kind.EMBEDDED)
    val marketplace: AddressKey,
    @Relation(Relation.Kind.EMBEDDED)
    val item: AddressKey,
    @Relation(Relation.Kind.EMBEDDED)
    val owner: AddressKey,
    val price: Long,
    val marketplaceFee: Long,
    val royalty: Long?,
    @Relation(Relation.Kind.EMBEDDED)
    val royaltyDestination: AddressKey?,

    override val discovered: Instant = Instant.now(),
    override var updated: Instant = Instant.MIN,
    override var modified: Instant = Instant.MIN,
) : BasicModel
