package money.tegro.market.operations

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.ItemDTO
import money.tegro.market.dto.TransactionRequestDTO

@Tag(name = "Item", description = "The API to get and interact with NFT items")
interface ItemOperations {
    @Operation(summary = "Get all items in the database")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ItemDTO::class))
                    )
                ]
            )
        ]
    )
    @Get("/{?pageable*}")
    suspend fun getAll(
        @Parameter(
            description = "Pageable properties"
        )
        pageable: Pageable
    ): Flow<ItemDTO>

    @Operation(summary = "Get item information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ItemDTO::class))
                    )
                ]
            )
        ]
    )
    @Get("/{item}")
    suspend fun getItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable item: String
    ): ItemDTO

    @Operation(summary = "Build a message to transfer an item")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = TransactionRequestDTO::class))
                    )
                ]
            )
        ]
    )
    @Get("/{item}/transfer")
    suspend fun transferItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable item: String,

        @Parameter(
            description = "Destination address, new owner of the item, can be base64(url) or raw",
            required = true
        )
        @QueryValue to: String,

        @Parameter(
            description = "Address were rest of the coins is sent, can be base64(url) or raw or null",
        )
        @QueryValue response: String?,
    ): TransactionRequestDTO

    @Operation(summary = "Build a message to put an item up for sale")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = TransactionRequestDTO::class))
                    )
                ]
            )
        ]
    )
    @Get("/{item}/sell")
    suspend fun sellItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        item: String,

        @Parameter(
            description = "Address were rest of the coins is sent, usually it's current item owner, can be base64(url) or raw",
            required = true
        )
        @QueryValue
        from: String,

        @Parameter(
            description = "Price of the item (in nanotons); This is the amount seller will receive, fees are added extra",
            required = true
        )
        @QueryValue
        price: Long,
    ): TransactionRequestDTO
}
