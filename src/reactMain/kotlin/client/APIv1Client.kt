package client

import browser.window
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.serialization.kotlinx.humanReadableSerializerModule
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ItemDTO
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.operations.APIv1Operations

object APIv1Client : APIv1Operations {
    private val json = Json { serializersModule = humanReadableSerializerModule }

    override fun listTopCollections(drop: Int?, take: Int?): Flow<CollectionDTO> =
        flow {
            window.fetch("http://localhost:8080/api/v1/collections/top")
                .await()
                .text()
                .await()
                .let { json.decodeFromString<List<CollectionDTO>>(it) }
                .forEach {
                    emit(it)
                }
        }

    override suspend fun getCollection(collection: String): CollectionDTO =
        window.fetch("http://localhost:8080/api/v1/collection/$collection")
            .await()
            .text()
            .await()
            .let { json.decodeFromString(it) }

    override fun listCollectionItems(collection: String, drop: Int?, take: Int?): Flow<ItemDTO> =
        flow {
            window.fetch("http://localhost:8080/api/v1/collection/$collection/items?drop=$drop&take=$take")
                .await()
                .text()
                .await()
                .let { json.decodeFromString<List<ItemDTO>>(it) }
                .forEach {
                    emit(it)
                }
        }

    override suspend fun getItem(item: String): ItemDTO =
        window.fetch("http://localhost:8080/api/v1/item/$item")
            .await()
            .text()
            .await()
            .let { json.decodeFromString(it) }

    override suspend fun transferItem(
        item: String,
        newOwner: String,
        responseDestination: String?
    ): TransactionRequestDTO =
        window.fetch(
            "http://localhost:8080/api/v1/item/$item/transfer?newOwner=$newOwner"
                    + responseDestination?.let { ("&responseDestination=$it") }.orEmpty()
        )
            .await()
            .text()
            .await()
            .let { json.decodeFromString(it) }

    override suspend fun sellItem(item: String, seller: String, price: BigInteger): TransactionRequestDTO =
        window.fetch("http://localhost:8080/api/v1/item/$item/sell?seller=$seller&price=$price")
            .await()
            .text()
            .await()
            .let { json.decodeFromString(it) }
}
