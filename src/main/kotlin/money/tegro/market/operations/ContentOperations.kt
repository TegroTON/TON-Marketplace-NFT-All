package money.tegro.market.operations

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.server.types.files.StreamedFile
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Content", description = "The API to get NFT collection/item content")
interface ContentOperations {
    @Operation(summary = "Get main collection image, its logo")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "image/*"
                    )
                ]
            )
        ]
    )
    @Get("/collection/{collection}/image")
    suspend fun getCollectionImage(
        @Parameter(
            description = "Collection address address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        collection: String
    ): StreamedFile

    @Operation(summary = "Get collection cover image, its banner")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "image/*"
                    )
                ]
            )
        ]
    )
    @Get("/collection/{collection}/cover_image")
    suspend fun getCollectionCoverImage(
        @Parameter(
            description = "Collection address address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        collection: String
    ): StreamedFile

    @Operation(summary = "Get main item image")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "image/*"
                    )
                ]
            )
        ]
    )
    @Get("/item/{item}/image")
    suspend fun getItemImage(
        @Parameter(
            description = "Collection address address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        item: String
    ): StreamedFile
}
