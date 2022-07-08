package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.mapper.CollectionMapper
import money.tegro.market.core.mapper.ItemMapper
import money.tegro.market.core.operations.CollectionOperations
import money.tegro.market.core.repository.*
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/collections")
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
    private val saleRepository: SaleRepository,
    private val royaltyRepository: RoyaltyRepository,

    private val collectionMapper: CollectionMapper,
    private val itemMapper: ItemMapper,
) : CollectionOperations {
    override fun getAll(pageable: Pageable): Flux<CollectionDTO> = collectionRepository.findAll(pageable)
        .flatMapMany { it.toFlux() }
        .flatMap {
            collectionMapper.map(
                collection = it,
                itemNumber = itemRepository.countByCollection(it.address),
                royalty = royaltyRepository.findById(it.address)
            )
        }

    override fun getCollection(collection: String): Mono<CollectionDTO> =
        collectionRepository.findById(AddrStd(collection))
            .flatMap {
                collectionMapper.map(
                    collection = it,
                    itemNumber = itemRepository.countByCollection(it.address),
                    royalty = royaltyRepository.findById(it.address)
                )
            }

    override fun getCollectionItems(collection: String, pageable: Pageable): Flux<ItemDTO> =
        collectionRepository.findById(AddrStd(collection))
            .flatMapMany { coll ->
                itemRepository.findByCollection(coll.address, pageable)
                    .flatMapMany { it.toFlux() }
                    .flatMap {
                        itemMapper.map(
                            item = it,
                            attributes = attributeRepository.findByItem(it.address),
                            royalty = if (it.collection is AddrStd) royaltyRepository.findById(it.collection as AddrStd) else royaltyRepository.findById(
                                it.address
                            ),
                            sale = (it.owner as? AddrStd)?.let { saleRepository.findById(it) } ?: Mono.empty(),
                        )
                    }
            }
}
