package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.ResourceNotFoundException
import dev.fritz2.repository.rest.restEntityOf
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.web.resource.TransactionRequestResource

object ItemTransferStore : RootStore<TransactionRequestDTO?>(null) {
    private val rest =
        restEntityOf(TransactionRequestResource, http("/api/v1/item"), initialId = "")

    val load = handle<Triple<String, String, String?>> { _, (item, newOwner, responseDestination) ->
        try {
            rest.load("$item/transfer?newOwner=$newOwner&responseDestination=$responseDestination")
        } catch (_: ResourceNotFoundException) {
            null
        }
    }

    val request = handle { _ ->
        current?.let { ConnectionStore.requestTransaction(it) }
        current
    }
}
