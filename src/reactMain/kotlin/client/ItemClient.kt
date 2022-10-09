package client

import browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.BasicItemDTO
import money.tegro.market.operations.ItemOperations

object ItemClient : ItemOperations {
    override suspend fun getBasicInfoByAddress(address: String): BasicItemDTO =
        window.fetch("http://localhost:8080/api/v1/item/$address/basic")
            .await()
            .text()
            .await()
            .let { Json.decodeFromString(it) }
}
