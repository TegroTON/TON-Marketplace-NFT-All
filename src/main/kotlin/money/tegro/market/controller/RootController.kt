package money.tegro.market.controller

import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.service.SaleService
import money.tegro.market.service.item.ItemContractService
import money.tegro.market.service.item.ItemMetadataService
import money.tegro.market.service.item.ItemSaleAddressService
import money.tegro.market.service.profile.ProfileOwnedItemService
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
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val itemContractService: ItemContractService,
    private val itemMetadataService: ItemMetadataService,
    private val itemSaleAddressService: ItemSaleAddressService,
    private val saleService: SaleService,
    private val profileOwnedItemService: ProfileOwnedItemService,
) {
    @RequestMapping("/")
    suspend fun index(
        model: Model,
    ): String {
        model.addAttribute("collections", collectionRepository.listAll().take(9).toList())

        return "index"
    }

    @RequestMapping("/collection/{address}")
    suspend fun collection(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val collection = MsgAddressInt(address)

        model.addAttribute("collection", collectionRepository.getByAddress(collection))
        model.addAttribute("items", collectionRepository.listItems(collection))

        return "collection"
    }

    @RequestMapping("/item/{address}")
    suspend fun item(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val item = MsgAddressInt(address)

        model.addAttribute("item", itemRepository.getByAddress(item))
        model.addAttribute("collection", collectionRepository.getItemCollection(item))

        return "item";
    }

    @RequestMapping("/profile/{address}")
    suspend fun profile(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val profile = MsgAddressInt(address)

        model.addAllAttributes(
            mapOf(
                "address" to profile.toRaw(),
                "addressShort" to profile.toShortFriendly(),
                "ownedItems" to profileOwnedItemService.get(profile)
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

        return "profile"
    }
}
