package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restQueryOf
import money.tegro.market.dto.ItemDTO
import money.tegro.market.web.resource.ItemResource

object CollectionItemsStore : RootStore<List<ItemDTO>>(emptyList()) {
    private val rest =
        restQueryOf<ItemDTO, String, String>(
            ItemResource,
            http("/api/v1/collection/"),
            initialId = ""
        ) {
            get("$it/items?take=25")
        }

    val query = handle<String> { _, collection ->
        rest.query(collection)
    }
}
