package client

import browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.TopCollectionDTO
import money.tegro.market.operations.CollectionOperations

object CollectionClient : CollectionOperations {
    override fun listTopCollections(): Flow<TopCollectionDTO> =
        flow {
            window.fetch("http://localhost:8080/api/v1/collections/top")
                .await()
                .text()
                .await()
                .let { Json.decodeFromString<List<TopCollectionDTO>>(it) }
                .forEach {
                    emit(it)
                }
        }

    override suspend fun getByAddress(address: String): CollectionDTO =
        window.fetch("http://localhost:8080/api/v1/collection/$address")
            .await()
            .text()
            .await()
            .let { Json.decodeFromString(it) }
}
