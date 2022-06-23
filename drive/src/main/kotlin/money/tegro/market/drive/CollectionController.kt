package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.operations.CollectionOperations
import money.tegro.market.core.repository.AttributeRepository
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.MsgAddressIntStd
import reactor.kotlin.core.publisher.toFlux

@Controller("/collections")
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
) : CollectionOperations {
    override fun getAll(pageable: Pageable?) = collectionRepository.findAll(pageable ?: Pageable.UNPAGED)
        .flatMapMany {
            it.toFlux().map { CollectionDTO(it, itemRepository.countByCollection(it.address)) }
        }

    override fun getCollection(collection: String) =
        collectionRepository.findByAddressStd(MsgAddressIntStd(collection))
            .map { CollectionDTO(it, itemRepository.countByCollection(it.address)) }

    override fun getCollectionItems(collection: String, pageable: Pageable?) =
        collectionRepository.findByAddressStd(MsgAddressIntStd(collection))
            .flatMapMany { coll ->
                itemRepository.findByCollection(coll.address, pageable ?: Pageable.UNPAGED)
                    .flatMapMany {
                        it.toFlux()
                            .map { ItemDTO(it, coll, attributeRepository.findByItem(it.address).toIterable()) }
                    }
            }
}
