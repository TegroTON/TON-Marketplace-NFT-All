package money.tegro.market.controller

import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import money.tegro.market.service.SaleService
import money.tegro.market.service.collection.*
import money.tegro.market.service.item.ItemContractService
import money.tegro.market.service.item.ItemMetadataService
import money.tegro.market.service.item.ItemSaleAddressService
import money.tegro.market.toRaw
import money.tegro.market.toShortFriendly
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.ton.bigint.BigInt
import org.ton.block.Coins
import org.ton.block.MsgAddressInt

@Controller
class RootController(
    private val collectionListService: CollectionListService,
    private val collectionContractService: CollectionContractService,
    private val collectionMetadataService: CollectionMetadataService,
    private val collectionItemOwnerNumberService: CollectionItemOwnerNumberService,
    private val collectionItemListService: CollectionItemListService,
    private val itemContractService: ItemContractService,
    private val itemMetadataService: ItemMetadataService,
    private val itemSaleAddressService: ItemSaleAddressService,
    private val saleService: SaleService,
) {
    @RequestMapping("/")
    suspend fun index(
        model: Model,
    ): String {
        model.addAttribute("topCollections",
            collectionListService.get()
                .mapNotNull {
                    collectionMetadataService.get(it)?.let { metadata ->
                        mapOf(
                            "link" to "/collection/${it.toRaw()}",
                            "name" to metadata.name,
                            "image" to metadata.image,
                        )
                    }
                }
                .take(9)
                .toList()
        )

        return "index"
    }

    @RequestMapping("/collection/{address}")
    suspend fun collection(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val collection = MsgAddressInt(address)
        val contract = collectionContractService.get(collection)
        val metadata = collectionMetadataService.get(collection)
        val itemOwnerNumber = collectionItemOwnerNumberService.get(collection)

        model.addAllAttributes(
            mapOf(
                "address" to collection.toRaw(),
                "addressShort" to collection.toShortFriendly(),
                "name" to (metadata?.name ?: "Untitled Collection"),
                "image" to metadata?.image,
                "description" to metadata?.description.orEmpty(),
                "ownerShort" to (contract?.owner?.toShortFriendly() ?: "Unknown"),
                "itemNumber" to (contract?.next_item_index ?: 0uL),
                "itemOwnerNumber" to itemOwnerNumber,
                "collectionItems" to collectionItemListService.get(collection)
                    .mapNotNull { it as? MsgAddressInt }
                    .mapNotNull { item ->
                        val itemSale = itemSaleAddressService.get(item)?.let { saleService.get(it) }

                        itemContractService.get(item)?.let { itemContract ->
                            itemMetadataService.get(item)?.let { itemMetadata ->
                                mapOf(
                                    "link" to "/item/${item.toRaw()}",
                                    "name" to itemMetadata.name,
                                    "image" to itemMetadata.image,
                                    "isOnSale" to (itemSale != null),
                                    "formattedPrice" to Coins.ofNano(itemSale?.full_price ?: BigInt.ZERO)
                                        .toString() + " TON"
                                )
                            }
                        }

                    }
                    .take(10)
                    .toList()
            )
        )

        return "collection"
    }
}
