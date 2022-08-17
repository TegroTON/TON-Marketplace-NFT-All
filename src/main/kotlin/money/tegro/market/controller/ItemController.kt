package money.tegro.market.controller

import money.tegro.market.dto.ItemDTO
import money.tegro.market.service.ItemService
import money.tegro.market.service.RoyaltyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressInt

@RestController
@RequestMapping("/api/v1/item")
class ItemController(
    private val itemService: ItemService,
    private val royaltyService: RoyaltyService,
) {
    @GetMapping("/{address}")
    suspend fun get(@PathVariable address: MsgAddressInt): ItemDTO {
        val contract = requireNotNull(itemService.getContract(address)) { "Unable to fetch item contract" }
        return ItemDTO(
            address,
            contract,
            requireNotNull(itemService.getMetadata(address)) { "Unable to parse item metadata" },
            (contract.collection as? MsgAddressInt)?.let { royaltyService.get(it) }
                ?: royaltyService.get(address)
        )
    }
}

