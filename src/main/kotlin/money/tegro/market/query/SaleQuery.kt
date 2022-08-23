package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.contract.SaleContract
import money.tegro.market.toRaw
import org.ton.block.MsgAddressInt

@GraphQLName("Sale")
data class SaleQuery(
    val marketplace: String?,
    val item: ItemQuery?,
    val owner: String?,
    val fullPrice: String,
    val marketplaceFee: String,
    val royaltyDestination: String?,
    val royalty: String,
) {
    constructor(it: SaleContract) : this(
        marketplace = it.marketplace.toRaw(),
        item = (it.item as? MsgAddressInt)?.let { ItemQuery(it) },
        owner = it.owner.toRaw(),
        fullPrice = it.fullPrice.toString(),
        marketplaceFee = it.marketplaceFee.toString(),
        royaltyDestination = it.royaltyDestination.toRaw(),
        royalty = it.royalty.toString(),
    )
}
