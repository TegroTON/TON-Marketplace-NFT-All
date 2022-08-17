package money.tegro.market.controller

import kotlinx.coroutines.flow.mapNotNull
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ShortItemDTO
import money.tegro.market.service.CollectionService
import money.tegro.market.service.RoyaltyService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressInt

@RestController
@RequestMapping("/api/v1/collection")
class CollectionController(
    private val collectionService: CollectionService,
    private val royaltyService: RoyaltyService,
) {
    @GetMapping("/", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun all() =
        collectionService.all()
            .mapNotNull {
                collectionService.getContract(it)?.let { contract ->
                    collectionService.getMetadata(it)?.let { metadata ->
                        CollectionDTO(it, contract, metadata, royaltyService.get(it))
                    }
                }
            }

    @GetMapping("/{address}")
    suspend fun get(@PathVariable address: MsgAddressInt) =
        CollectionDTO(
            address,
            requireNotNull(collectionService.getContract(address)) { "Unable to fetch collection contract" },
            requireNotNull(collectionService.getMetadata(address)) { "Unable to parse collection metadata" },
            royaltyService.get(address)
        )

    @GetMapping("/{address}/{index}")
    suspend fun getItem(@PathVariable address: MsgAddressInt, @PathVariable index: Long): ShortItemDTO? =
        (collectionService.getItemAddress(address, index.toULong()) as? MsgAddressInt)?.let {
            ShortItemDTO(index.toULong(), it)
        }

    @GetMapping("/{address}/all", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun listItems(@PathVariable address: MsgAddressInt) =
        collectionService.listItemAddresses(address)
            .mapNotNull { (it.second as? MsgAddressInt)?.let { item -> ShortItemDTO(it.first, item) } }
}

