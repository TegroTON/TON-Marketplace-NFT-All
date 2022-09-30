package money.tegro.market.controller

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import money.tegro.market.dropTake
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.repository.ProfileRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.ton.block.MsgAddressInt

@Controller
class RootController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val profileRepository: ProfileRepository,
) {
    @RequestMapping("/")
    suspend fun index(
        model: Model,
    ): String {
        model.addAttribute("collections", collectionRepository.listAll().take(9))

        return "index"
    }

    @RequestMapping("/collection/{address}")
    suspend fun collection(
        model: Model,
        @PathVariable(required = true) address: MsgAddressInt,
        @RequestParam(defaultValue = "0") drop: Int,
        @RequestParam(defaultValue = "16") take: Int,
    ): String {
        model.addAttribute("collection", collectionRepository.getByAddress(address))
        model.addAttribute("items", itemRepository.listCollectionItems(address).dropTake(drop, take).toList())
        model.addAllAttributes(
            mapOf(
                "drop" to drop,
                "take" to take,
            )
        )

        return "collection"
    }

    @RequestMapping("/item/{address}")
    suspend fun item(
        model: Model,
        @PathVariable(required = true) address: MsgAddressInt,
    ): String {
        model.addAttribute("item", itemRepository.getByAddress(address))
        model.addAttribute("collection", itemRepository.getItemCollection(address))

        return "item";
    }

    @RequestMapping("/profile/{address}")
    suspend fun profile(
        model: Model,
        @PathVariable(required = true) address: MsgAddressInt,
        @RequestParam(defaultValue = "0") drop: Int,
        @RequestParam(defaultValue = "16") take: Int,
    ): String {
        model.addAttribute("profile", profileRepository.getByAddress(address))
        model.addAttribute("items", itemRepository.listItemsOwnedBy(address).dropTake(drop, take).toList())
        model.addAllAttributes(
            mapOf(
                "drop" to drop,
                "take" to take,
            )
        )

        return "profile"
    }

    @RequestMapping("/explore")
    suspend fun profile(
        model: Model,
    ): String {
        model.addAttribute("collections", collectionRepository.listAll())

        return "explore"
    }
}
