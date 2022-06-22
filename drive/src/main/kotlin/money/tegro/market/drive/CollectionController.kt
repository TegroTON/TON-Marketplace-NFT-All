package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
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
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.MsgAddressIntStd
import reactor.kotlin.core.publisher.toFlux

@Tag(name = "Collection", description = "Set of methods to interact with NFT collections")
@Controller("/collection")
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) {
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
    ) = collectionRepository.findAll(pageable ?: Pageable.UNPAGED)
        .flatMapMany {
            it.toFlux().map { CollectionDTO(it) }
        }

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
    @Get("/{address}")
    fun getCollection(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String
    ) =
        collectionRepository.findByAddressStd(MsgAddressIntStd(address))
            .map { CollectionDTO(it) }

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
    @Get("/{address}/items")
    fun getCollectionItems(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String,
        @Parameter(
            description = "Pageable properties"
        )
        @QueryValue(defaultValue = "null") pageable: Pageable?
    ) =
        collectionRepository.findByAddressStd(MsgAddressIntStd(address))
            .flatMapMany { collection ->
                itemRepository.findByCollection(collection.address, pageable ?: Pageable.UNPAGED)
                    .flatMapMany {
                        it.toFlux().map { ItemDTO(it, collection) }
                    }
            }
}
