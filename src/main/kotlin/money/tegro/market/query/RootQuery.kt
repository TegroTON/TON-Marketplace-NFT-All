package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.service.collection.CollectionListService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.ton.block.MsgAddressInt

@Component
class RootQuery : Query {
    suspend fun collections(
        drop: Int? = null,
        take: Int? = null,
        @GraphQLIgnore @Autowired collectionListService: CollectionListService,
    ) =
        collectionListService.get()
            .dropTake(drop, take)
            .map { CollectionQuery(it) }
            .toList()

    suspend fun collection(address: String) =
        CollectionQuery(MsgAddressInt(address))

    suspend fun item(address: String) =
        ItemQuery(MsgAddressInt(address))

    suspend fun profile(address: String) =
        ProfileQuery(MsgAddressInt(address))
}
