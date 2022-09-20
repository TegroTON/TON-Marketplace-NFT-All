package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.service.profile.ProfileOwnedCollectionService
import money.tegro.market.service.profile.ProfileOwnedItemService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.block.MsgAddressInt

@GraphQLName("Profile")
data class ProfileQuery(
    @GraphQLIgnore
    val address: MsgAddressInt
) {
    @GraphQLName("address")
    val addressString: String = address.toRaw()

    suspend fun ownedItems(
        drop: Int? = null,
        take: Int? = null,
        @GraphQLIgnore @Autowired profileOwnedItemService: ProfileOwnedItemService,
    ) =
        profileOwnedItemService.get(address)
            .dropTake(drop, take)
            .map { ItemQuery(it) }
            .toList()

    suspend fun collections(
        drop: Int? = null,
        take: Int? = null,
        @GraphQLIgnore @Autowired profileOwnedCollectionService: ProfileOwnedCollectionService,
    ) =
        profileOwnedCollectionService.get(address)
            .dropTake(drop, take)
            .map { CollectionQuery(it) }
            .toList()
}
