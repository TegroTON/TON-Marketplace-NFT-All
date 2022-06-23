package money.tegro.market.core.operations

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
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.dto.TransactionRequestDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
    @Get("/")
    fun getAll(
        @Parameter(
            description = "Pageable properties"
        )
        @QueryValue(defaultValue = "null") pageable: Pageable?
    ): Flux<ItemDTO>

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
    fun getItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable item: String
    ): Mono<ItemDTO>

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
    fun transferItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable item: String,

        @Parameter(
            description = "Owner's address, this is where excess coins will be returned to, can be base64(url) or raw",
            required = true
        )
        @QueryValue from: String,

        @Parameter(
            description = "Destination address, new owner of the item, can be base64(url) or raw"
        )
        @QueryValue to: String,
    ): Mono<TransactionRequestDTO>
}
