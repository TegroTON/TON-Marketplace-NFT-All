package money.tegro.market.controller

import money.tegro.market.dto.BasicItemDTO
import money.tegro.market.dto.ImageDTO
import money.tegro.market.operations.ItemOperations
import money.tegro.market.repository.ItemRepository
import money.tegro.market.toRaw
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressInt

@RestController
class ItemController(
    private val itemRepository: ItemRepository,
) : ItemOperations {
    @RequestMapping("/api/v1/item/{address}/basic")
    override suspend fun getBasicInfoByAddress(@PathVariable address: String): BasicItemDTO {
        val item = MsgAddressInt(address)
        val contract = requireNotNull(itemRepository.getContract(item))
        val metadata = itemRepository.getMetadata(item)

        return BasicItemDTO(
            address = item.toRaw(),
            name = metadata?.name ?: "Item no. ${contract.index}",
            image = ImageDTO(
                original = metadata?.image
            ),
        )
    }
}
