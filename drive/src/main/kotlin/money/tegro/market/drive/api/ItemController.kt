package money.tegro.market.drive.api

import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.drive.model.ItemModel
import org.springframework.web.bind.annotation.*
import org.ton.block.MsgAddressIntStd


@RestController
@RequestMapping("/api/v1/item")
class ItemController(
    val itemInfoRepository: ItemInfoRepository,
) {
    @GetMapping("/")
    fun indexAll(@RequestParam(defaultValue = "100") limit: Int) =
        itemInfoRepository.findAll()
            .orEmpty()
            .filter { it.initialized }
            .sortedBy { it.index }
            .take(minOf(limit, 100))
            .map { ItemModel(it) }

    @GetMapping("/{address}")
    fun getItem(@PathVariable address: String) =
        itemInfoRepository.findByAddress(MsgAddressIntStd(address))
            ?.let { ItemModel(it) }
}
