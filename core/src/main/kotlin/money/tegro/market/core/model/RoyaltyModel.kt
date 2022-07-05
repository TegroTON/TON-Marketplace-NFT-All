package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.dto.toKey
import money.tegro.market.core.key.AddressKey
import java.time.Instant

@MappedEntity("royalties")
@Schema(hidden = true)
data class RoyaltyModel(
    @EmbeddedId
    val address: AddressKey,

    val numerator: Int,

    val denominator: Int,

    @Relation(Relation.Kind.EMBEDDED)
    val destination: AddressKey,


    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now()
) {
    companion object {
        @JvmStatic
        fun of(royalty: NFTRoyalty): RoyaltyModel? = royalty.destination.toKey()?.let { destination ->
            royalty.address.toKey()?.let { address ->
                RoyaltyModel(
                    address = address,
                    numerator = royalty.numerator,
                    denominator = royalty.denominator,
                    destination = destination
                )
            }
        }
    }
}
