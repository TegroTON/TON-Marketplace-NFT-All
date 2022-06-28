package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTSale
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
    val royalty: Long?,
    @Relation(Relation.Kind.EMBEDDED)
    val royaltyDestination: AddressKey?,

    val discovered: Instant = Instant.now(),
    var updated: Instant = Instant.MIN,
) {
    constructor(it: NFTSale) : this(
        AddressKey.of(it.address),
        AddressKey.of(it.marketplace),
        AddressKey.of(it.item),
        AddressKey.of(it.owner),
        it.price,
        it.marketplaceFee,
        it.royalty,
        it.royaltyDestination?.let { AddressKey.of(it) },
    )
}
