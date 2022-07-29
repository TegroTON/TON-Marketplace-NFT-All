package money.tegro.market.operations

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import money.tegro.market.dto.AccountDTO
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ItemDTO

@Tag(name = "Account", description = "Set of methods to interact with user accounts")
interface AccountOperations {
    @Operation(summary = "Get account information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = AccountDTO::class)
                    )
                ]
            )
        ]
    )
    @Get("/{account}")
    suspend fun getAccount(
        @Parameter(
            description = "Account address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        account: String
    ): AccountDTO

    @Operation(summary = "Get all items owned by the account")
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
    @Get("/{account}/items")
    suspend fun getAccountItems(
        @Parameter(
            description = "Account address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        account: String
    ): Flow<ItemDTO>

    @Operation(summary = "Get all collections owned by the account")
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
    @Get("/{account}/collections")
    suspend fun getAccountCollections(
        @Parameter(
            description = "Account address, can be base64(url) or raw",
            required = true
        )
        @PathVariable
        account: String
    ): Flow<CollectionDTO>
}
