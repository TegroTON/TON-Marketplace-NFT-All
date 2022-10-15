package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restEntityOf
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.web.resource.CollectionResource

object CollectionStore : RootStore<CollectionDTO?>(null) {
    private val rest =
        restEntityOf(CollectionResource, http("http://localhost:8080/api/v1/collection"), initialId = "")

    val load = handle<String> { _, address ->
        rest.load(address)
    }
}
