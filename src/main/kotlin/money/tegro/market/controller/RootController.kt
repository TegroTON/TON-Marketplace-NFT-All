package money.tegro.market.controller

import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.repository.ProfileRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
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
        model.addAttribute("collections", collectionRepository.listAll())

        return "index"
    }

    @RequestMapping("/collection/{address}")
    suspend fun collection(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val collection = MsgAddressInt(address)

        model.addAttribute("collection", collectionRepository.getByAddress(collection))
        model.addAttribute("items", itemRepository.listCollectionItems(collection))

        return "collection"
    }

    @RequestMapping("/item/{address}")
    suspend fun item(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val item = MsgAddressInt(address)

        model.addAttribute("item", itemRepository.getByAddress(item))
        model.addAttribute("collection", itemRepository.getItemCollection(item))

        return "item";
    }

    @RequestMapping("/profile/{address}")
    suspend fun profile(
        model: Model,
        @PathVariable(required = true) address: String,
    ): String {
        val profile = MsgAddressInt(address)

        model.addAttribute("profile", profileRepository.getByAddress(profile))
        model.addAttribute("items", itemRepository.listItemsOwnedBy(profile))

        return "profile"
    }
}
