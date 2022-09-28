package money.tegro.market.controller

import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import money.tegro.market.service.SaleService
import money.tegro.market.service.collection.*
import money.tegro.market.service.item.ItemContractService
import money.tegro.market.service.item.ItemMetadataService
import money.tegro.market.service.item.ItemOwnerAddressService
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
    private val itemOwnerAAddressService: ItemOwnerAddressService,
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
                "coverImage" to (metadata?.coverImage ?: metadata?.image),
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

    @RequestMapping("/item/{address}")
    suspend fun item(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val item = MsgAddressInt(address)
        val contract = itemContractService.get(item)
        val metadata = itemMetadataService.get(item)
        val ownerAddress = itemOwnerAAddressService.get(item)
        val saleAddress = itemSaleAddressService.get(item)
        val sale = saleAddress?.let { saleService.get(it) }
        val collectionAddress = (contract?.collection as? MsgAddressInt)
        val collectionMetadata = collectionAddress?.let { collectionMetadataService.get(it) }

        model.addAllAttributes(
            mapOf(
                "address" to item.toRaw(),
                "addressShort" to item.toShortFriendly(),
                "isInCollection" to (collectionAddress != null),
                "collectionLink" to if (collectionAddress != null) "/collection/${collectionAddress.toRaw()}" else "/explore",
                "collectionName" to (collectionMetadata?.name ?: "Untitled Collection"),
                "collectionImage" to collectionMetadata?.image,
                "name" to (metadata?.name ?: "Item no. ${contract?.index ?: 0uL}"),
                "description" to metadata?.description,
                "image" to metadata?.image,
                "attributes" to metadata?.attributes.orEmpty()
                    .map { mapOf("trait" to it.trait, "value" to it.value) },
                "ownerShort" to (ownerAddress?.toShortFriendly() ?: "Unknown"),
                "isOnSale" to (saleAddress is MsgAddressInt),
                "saleAddress" to saleAddress?.toRaw(),
                "saleAddressShort" to saleAddress?.toShortFriendly(),
                "fullPrice" to Coins.ofNano(sale?.full_price ?: BigInt.ZERO).toString() + " TON",
            )
        )

        return "item";
    }
}
