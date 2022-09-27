package money.tegro.market.controller

import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import money.tegro.market.service.collection.CollectionContractService
import money.tegro.market.service.collection.CollectionItemOwnerNumberService
import money.tegro.market.service.collection.CollectionListService
import money.tegro.market.service.collection.CollectionMetadataService
import money.tegro.market.toRaw
import money.tegro.market.toShortFriendly
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.ton.block.MsgAddressInt

@Controller
class RootController(
    private val collectionListService: CollectionListService,
    private val collectionContractService: CollectionContractService,
    private val collectionMetadataService: CollectionMetadataService,
    private val collectionItemOwnerNumberService: CollectionItemOwnerNumberService,
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
            )
        )

        return "collection"
    }
}
