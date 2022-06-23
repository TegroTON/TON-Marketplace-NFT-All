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
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Tag(name = "Collection", description = "Set of methods to interact with NFT collections")
interface CollectionOperations {
    @Operation(summary = "List all available collections")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = CollectionDTO::class))
                    )
                ]
            )
        ]
    )
    @Get("/")
    fun getAll(
        @Parameter(description = "Pageable properties")
        @QueryValue(defaultValue = "null") pageable: Pageable?
    ): Flux<CollectionDTO>

    @Operation(summary = "Get collection information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = CollectionDTO::class))
                ]
            )
        ]
    )
    @Get("/{collection}")
    fun getCollection(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable collection: String
    ): Mono<CollectionDTO>

    @Operation(summary = "Get collection items")
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
    @Get("/{collection}/items")
    fun getCollectionItems(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable collection: String,
        
        @Parameter(
            description = "Pageable properties"
        )
        @QueryValue(defaultValue = "null") pageable: Pageable?
    ): Flux<ItemDTO>
}
