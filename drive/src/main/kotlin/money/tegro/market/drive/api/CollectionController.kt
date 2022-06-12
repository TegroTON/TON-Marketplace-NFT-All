package money.tegro.market.drive.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.drive.model.CollectionModel
import money.tegro.market.drive.model.ItemModel
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.ton.block.MsgAddressIntStd


@Tag(name = "Collection", description = "The API to get and interact with NFT collections")
@RestController
@RequestMapping("/api/v1/collection")
class CollectionController(
    val collectionInfoRepository: CollectionInfoRepository,
) {
    @Operation(summary = "Get collection information")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Successful operation",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = CollectionModel::class))
                ]
            )
        ]
    )
    @GetMapping("/{address}")
    fun getCollection(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String
    ) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))
            ?.let { CollectionModel(it) }


    @Operation(summary = "Get collection items")
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
    @GetMapping("/{address}/items")
    @Transactional
    fun getCollectionItems(
        @Parameter(
            description = "Collection address, can be base64(url) or raw",
            required = true
        )
        @PathVariable address: String,
        @Parameter(
            description = "Maximum number of entities returned"
        )
        @RequestParam(defaultValue = "100") limit: Int
    ) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))?.items
            .orEmpty()
            .filter { it.initialized }
            .sortedBy { it.index }
            .take(minOf(limit, 100))
            .map { ItemModel(it) }
}
