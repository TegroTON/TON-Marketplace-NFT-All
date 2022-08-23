package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.service.CollectionService
import money.tegro.market.service.RoyaltyService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.block.MsgAddressInt

@GraphQLName("Collection")
data class CollectionQuery(
    @GraphQLIgnore
    val address: MsgAddressInt
) {
    @GraphQLName("address")
    val addressString: String = address.toRaw()

    suspend fun contract(
        @GraphQLIgnore @Autowired collectionService: CollectionService
    ) =
        collectionService.getContract(address)?.let { CollectionContractQuery(it) }

    suspend fun metadata(
        @GraphQLIgnore @Autowired collectionService: CollectionService
    ) =
        collectionService.getMetadata(address)?.let { CollectionMetadataQuery(it) }

    suspend fun royalty(
        @GraphQLIgnore @Autowired royaltyService: RoyaltyService
    ) =
        royaltyService.get(address)?.let { RoyaltyQuery(it) }

    suspend fun items(
        @GraphQLIgnore @Autowired collectionService: CollectionService,
        drop: Int? = null,
        take: Int? = null,
    ) =
        collectionService.listItemAddresses(address)
            .dropTake(drop, take)
            .mapNotNull { info -> (info as? MsgAddressInt)?.let { ItemQuery(it) } }
            .toList()
}
