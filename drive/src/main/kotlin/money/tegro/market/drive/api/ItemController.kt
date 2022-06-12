package money.tegro.market.drive.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.drive.model.ItemModel
import org.springframework.web.bind.annotation.*
import org.ton.block.MsgAddressIntStd


@Tag(name = "Item", description = "The API to get and interact with NFT items")
@RestController
@RequestMapping("/api/v1/item")
class ItemController(
    val itemInfoRepository: ItemInfoRepository,
) {
    @Operation(summary = "Get all items in the database")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ItemModel::class))
                    )
                ]
            )
        ]
    )
    @GetMapping("/")
    fun indexAll(
        @Parameter(
            description = "Maximum number of entities returned"
        )
        @RequestParam(defaultValue = "100") limit: Int
    ) =
        itemInfoRepository.findAll()
            .orEmpty()
            .filter { it.initialized }
            .sortedBy { it.index }
            .take(minOf(limit, 100))
            .map { ItemModel(it) }


    @Operation(summary = "Get item information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = ItemModel::class))
                    )
                ]
            )
        ]
    )
    @GetMapping("/{address}")
    fun getItem(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String
    ) =
        itemInfoRepository.findByAddress(MsgAddressIntStd(address))
            ?.let { ItemModel(it) }
}
