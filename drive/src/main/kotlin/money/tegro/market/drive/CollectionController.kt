package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.operations.CollectionOperations
import money.tegro.market.core.repository.*
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Controller("/collections")
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
    private val saleRepository: SaleRepository,
) : CollectionOperations {
    override fun getAll(pageable: Pageable): Flux<CollectionDTO> = collectionRepository.findAll(pageable)
        .flatMapMany {
            it.toFlux().map { CollectionDTO(it, itemRepository.countByCollection(it.address)) }
        }

    override fun getCollection(collection: String): Mono<CollectionDTO> =
        collectionRepository.findByAddressStd(AddrStd(collection))
            .flatMap { mono { CollectionDTO(it, itemRepository.countByCollection(it.address)) } }

    override fun getCollectionItems(collection: String, pageable: Pageable): Flux<ItemDTO> =
        collectionRepository.findByAddressStd(AddrStd(collection))
            .flatMapMany { coll ->
                itemRepository.findByCollection(coll.address, pageable)
                    .flatMapMany {
                        it.toFlux()
                            .flatMap {
                                mono {
                                    ItemDTO(
                                        it,
                                        coll,
                                        attributeRepository.findByItem(it.address).collectList().awaitSingle(),
                                        saleRepository.findByItem(it.address).awaitSingleOrNull(),
                                    )
                                }
                            }
                    }
            }
}
