package money.tegro.market.web.store

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.fritz2.core.RootStore
import dev.fritz2.remote.http
import dev.fritz2.repository.ResourceNotFoundException
import dev.fritz2.repository.rest.restEntityOf
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.web.resource.TransactionRequestResource

object ItemSaleRequestStore : RootStore<TransactionRequestDTO?>(null) {
    private val rest =
        restEntityOf(TransactionRequestResource, http("/api/v1/item"), initialId = "")

    val load = handle<Triple<String, String, BigInteger>> { _, (item, seller, price) ->
        try {
            rest.load("$item/sell?seller=$seller&price=$price")
        } catch (_: ResourceNotFoundException) {
            null
        }
    }
}
