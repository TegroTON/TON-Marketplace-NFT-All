package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restQueryOf
import money.tegro.market.dto.ItemDTO
import money.tegro.market.web.resource.ItemResource

object ProfileItemsStore : RootStore<List<ItemDTO>>(emptyList()) {
    private val rest =
        restQueryOf<ItemDTO, String, String>(
            ItemResource,
            http("http://localhost:8080/api/v1/profile/"),
            initialId = ""
        ) {
            get("$it/items/owned?take=8")
        }

    val query = handle<String> { _, profile ->
        rest.query(profile)
    }
}
