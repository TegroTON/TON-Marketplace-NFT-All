package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restEntityOf
import money.tegro.market.dto.ItemDTO
import money.tegro.market.web.resource.ItemResource

object ItemStore : RootStore<ItemDTO?>(null) {
    private val rest =
        restEntityOf(ItemResource, http("http://localhost:8080/api/v1/item"), initialId = "")

    val load = handle<String> { _, address ->
        rest.load(address).also {
            it.collection?.let { CollectionStore.load(it) }
        }
    }
}
