package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.operations.ItemOperations
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.MsgAddressIntStd
import reactor.kotlin.core.publisher.toFlux

@Controller("/item")
class ItemController(
    val collectionRepository: CollectionRepository,
    val itemRepository: ItemRepository,
) : ItemOperations {
    override fun getAll(pageable: Pageable?) =
        itemRepository.findAll(pageable ?: Pageable.UNPAGED).flatMapMany {
            it.toFlux().map { item ->
                ItemDTO(item, item.collection?.let { collectionRepository.findById(it).block() })
            }
        }

    override fun getItem(address: String) =
        itemRepository.findByAddressStd(MsgAddressIntStd(address))
            .map { ItemDTO(it, it.collection?.let { collectionRepository.findById(it).block() }) }
}
