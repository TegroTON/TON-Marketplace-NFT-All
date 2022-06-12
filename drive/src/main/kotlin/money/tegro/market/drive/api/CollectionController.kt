package money.tegro.market.drive.api

import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.findByAddress
import money.tegro.market.drive.model.CollectionModel
import money.tegro.market.drive.model.ItemModel
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.ton.block.MsgAddressIntStd

@RestController
@RequestMapping("/api/v1/collection")
class CollectionController(
    val collectionInfoRepository: CollectionInfoRepository,
) {
    @GetMapping("/{address}")
    fun getCollection(@PathVariable address: String) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))
            ?.let { CollectionModel(it) }

    @GetMapping("/{address}/items")
    @Transactional
    fun getCollectionItems(@PathVariable address: String, @RequestParam(defaultValue = "100") limit: Int) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))?.items
            .orEmpty()
            .filter { it.initialized }
            .sortedBy { it.index }
            .take(minOf(limit, 100))
            .map { ItemModel(it) }
}
