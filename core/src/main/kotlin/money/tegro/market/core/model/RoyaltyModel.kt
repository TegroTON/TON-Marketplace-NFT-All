package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.key.AddressKey
import java.time.Instant

@MappedEntity("ROYALTIES")
@Schema(hidden = true)
data class RoyaltyModel(
    @EmbeddedId
    val address: AddressKey,

    var numerator: Int,
    var denominator: Int,
    @Relation(Relation.Kind.EMBEDDED)
    var destination: AddressKey,

    val discovered: Instant = Instant.now(),
    var updated: Instant = Instant.MIN
) {
    constructor(address: AddressKey, royalty: NFTRoyalty) : this(
        address,
        royalty.numerator,
        royalty.denominator,
        AddressKey.of(royalty.destination)
    )
}
