package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.service.collection.CollectionContractService
import money.tegro.market.service.collection.CollectionItemListService
import money.tegro.market.service.collection.CollectionItemOwnerNumberService
import money.tegro.market.service.collection.CollectionMetadataService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.block.MsgAddressInt

@GraphQLName("Collection")
data class CollectionQuery(
    @GraphQLIgnore
    val address: MsgAddressInt
) {
    @GraphQLName("address")
    val addressString: ID = ID(address.toRaw())

    suspend fun itemNumber(
        @GraphQLIgnore @Autowired collectionContractService: CollectionContractService,
    ) =
        collectionContractService.get(address)?.next_item_index?.toString()

    suspend fun ownerNumber(
        @GraphQLIgnore @Autowired collectionItemOwnerNumberService: CollectionItemOwnerNumberService,
    ) =
        collectionItemOwnerNumberService.get(address).toString()

    suspend fun owner(
        @GraphQLIgnore @Autowired collectionContractService: CollectionContractService,
    ) =
        collectionContractService.get(address)?.owner?.toRaw()

    suspend fun name(
        @GraphQLIgnore @Autowired collectionMetadataService: CollectionMetadataService,
    ) =
        collectionMetadataService.get(address)?.name

    suspend fun description(
        @GraphQLIgnore @Autowired collectionMetadataService: CollectionMetadataService,
    ) =
        collectionMetadataService.get(address)?.description

    suspend fun image(
        @GraphQLIgnore @Autowired collectionMetadataService: CollectionMetadataService,
    ) =
        collectionMetadataService.get(address)?.image

    suspend fun coverImage(
        @GraphQLIgnore @Autowired collectionMetadataService: CollectionMetadataService,
    ) =
        collectionMetadataService.get(address)?.coverImage

    suspend fun items(
        drop: Int? = null,
        take: Int? = null,
        @GraphQLIgnore @Autowired collectionItemListService: CollectionItemListService,
    ) =
        collectionItemListService.get(address)
            .dropTake(drop, take)
            .mapNotNull { info -> (info as? MsgAddressInt)?.let { ItemQuery(it) } }
            .toList()
}
