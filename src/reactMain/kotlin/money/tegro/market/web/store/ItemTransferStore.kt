package money.tegro.market.web.store

import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.rest.restEntityOf
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.web.resource.TransactionRequestResource

object ItemTransferStore : RootStore<TransactionRequestDTO?>(null) {
    private val rest =
        restEntityOf(TransactionRequestResource, http("http://localhost:8080/api/v1/item"), initialId = "")

    val load = handle<Triple<String, String, String?>> { _, (item, newOwner, responseDestination) ->
        rest.load("$item/transfer?newOwner=$newOwner&responseDestination=$responseDestination")
    }

    val request = handle<Triple<String, String, String?>> { _, args ->
        console.log("request $args")
        load(args)
        current?.also {
            console.log("connection $args")
            ConnectionStore.requestTransaction(it)
        }
    }
}
