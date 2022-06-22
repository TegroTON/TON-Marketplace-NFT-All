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
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.MsgAddressIntStd
import reactor.kotlin.core.publisher.toFlux

@Tag(name = "Item", description = "The API to get and interact with NFT items")
@Controller("/item")
class ItemController(
    val collectionRepository: CollectionRepository,
    val itemRepository: ItemRepository,
) {
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
    ) =
        itemRepository.findAll(pageable ?: Pageable.UNPAGED).flatMapMany {
            it.toFlux().map { item ->
                ItemDTO(item, item.collection?.let { collectionRepository.findById(it).block() })
            }
        }


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
    @Get("/{address}")
    fun getItem(
        @Parameter(
            description = "Item address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String
    ) =
        itemRepository.findByAddressStd(MsgAddressIntStd(address))
            .map { ItemDTO(it, it.collection?.let { collectionRepository.findById(it).block() }) }
}
