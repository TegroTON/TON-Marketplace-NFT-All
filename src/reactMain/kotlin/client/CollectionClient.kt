package client

import browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.TopCollectionDTO
import money.tegro.market.operations.CollectionOperations

object CollectionClient : CollectionOperations {
    override suspend fun listTopCollections(): Flow<TopCollectionDTO> =
        window.fetch("http://localhost:8080/api/v1/collections/top")
            .await()
            .text()
            .await()
            .let { Json.decodeFromString<List<TopCollectionDTO>>(it) }
            .asFlow()
}
