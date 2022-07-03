package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.dto.toKey
import money.tegro.market.core.key.AddressKey
import java.time.Instant

@MappedEntity("SALES")
@Schema(hidden = true)
data class SaleModel(
    @EmbeddedId
    val address: AddressKey,

    @Relation(Relation.Kind.EMBEDDED)
    val marketplace: AddressKey,

    @Relation(Relation.Kind.EMBEDDED)
    val item: AddressKey,

    @Relation(Relation.Kind.EMBEDDED)
    val owner: AddressKey,

    val fullPrice: Long,

    val marketplaceFee: Long,

    val royalty: Long,

    @Relation(Relation.Kind.EMBEDDED)
    val royaltyDestination: AddressKey?,

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.MIN,
) {
    companion object {
        // This absolutely hideous stairway to heaven is necessary in order to ensure that valid representation of contract data
        // is precisely mapped to a valid model used by the market. If not for this, messy null-check spaghetti would leak into
        // the business logic
        @JvmStatic
        fun of(sale: NFTSale): SaleModel? = sale.address.toKey()?.let { address ->
            sale.marketplace.toKey()?.let { marketplace ->
                sale.item.toKey()?.let { item ->
                    sale.owner.toKey()?.let { owner ->
                        SaleModel(
                            address,
                            marketplace,
                            item,
                            owner,
                            sale.fullPrice,
                            sale.marketplaceFee,
                            sale.royalty,
                            sale.royaltyDestination.toKey() // It's okay if this one is null
                        )
                    }
                }
            }
        }
    }
}
