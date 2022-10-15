package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restQueryOf
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.web.resource.CollectionResource

object TopCollectionsStore : RootStore<List<CollectionDTO>>(emptyList()) {
    private val rest =
        restQueryOf<CollectionDTO, String, Unit>(
            CollectionResource,
            http("http://localhost:8080/api/v1/collections/top?take=9"),
            initialId = ""
        )

    val query = handle { _ ->
        rest.query(Unit)
    }

    init {
        query()
    }
}
