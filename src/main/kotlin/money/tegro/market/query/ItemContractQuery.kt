package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.contract.ItemContract
import money.tegro.market.toRaw
import org.ton.block.MsgAddressInt

@GraphQLName("ItemContract")
data class ItemContractQuery(
    val initialized: Boolean,
    val index: String,
    val collection: CollectionQuery?,
    val owner: String?,
) {
    constructor(it: ItemContract) : this(
        initialized = it.initialized,
        index = it.index.toString(),
        collection = (it.collection as? MsgAddressInt)?.let { CollectionQuery(it) },
        owner = it.owner.toRaw(),
    )
}
