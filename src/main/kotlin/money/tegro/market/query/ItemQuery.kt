package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.service.ItemService
import money.tegro.market.service.RoyaltyService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.block.MsgAddressInt

@GraphQLName("Item")
data class ItemQuery(
    @GraphQLIgnore
    val address: MsgAddressInt,
) {
    @GraphQLName("address")
    val addressString: String = address.toRaw()

    suspend fun contract(
        @GraphQLIgnore @Autowired itemService: ItemService
    ) =
        itemService.getContract(address)?.let { ItemContractQuery(it) }

    suspend fun metadata(
        @GraphQLIgnore @Autowired itemService: ItemService
    ) =
        itemService.getMetadata(address)?.let { ItemMetadataQuery(it) }

    suspend fun royalty(
        @GraphQLIgnore @Autowired itemService: ItemService,
        @GraphQLIgnore @Autowired royaltyService: RoyaltyService,
    ) =
        (itemService.getContract(address)?.collection as? MsgAddressInt)?.let { collection ->
            royaltyService.get(collection)?.let { RoyaltyQuery(it) }
        }
            ?: royaltyService.get(address)?.let { RoyaltyQuery(it) }
}
