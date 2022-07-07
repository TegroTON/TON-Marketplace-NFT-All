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
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/collections")
class CollectionController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
    private val saleRepository: SaleRepository,
    private val royaltyRepository: RoyaltyRepository,
) : CollectionOperations {
    override fun getAll(pageable: Pageable): Flux<CollectionDTO> = collectionRepository.findAll(pageable)
        .flatMapMany {
            it.toFlux().flatMap {
                mono {
                    CollectionDTO(
                        it,
                        royaltyRepository.findById(it.address).awaitSingleOrNull(),
                    )
                }
            }
        }

    override fun getCollection(collection: String): Mono<CollectionDTO> =
        collectionRepository.findById(AddrStd(collection))
            .flatMap {
                mono {
                    CollectionDTO(
                        it,
                        royaltyRepository.findById(it.address).awaitSingleOrNull(),
                    )
                }
            }

    override fun getCollectionItems(collection: String, pageable: Pageable): Flux<ItemDTO> =
        collectionRepository.findById(AddrStd(collection))
            .flatMapMany { coll ->
                itemRepository.findByCollection(coll.address, pageable)
                    .flatMapMany {
                        it.toFlux()
                            .flatMap {
                                mono {
                                    ItemDTO(
                                        it,
                                        saleRepository.findByItem(it.address).awaitSingleOrNull(),
                                        (it.collection as? AddrStd)?.let {
                                            royaltyRepository.findById(it).awaitSingleOrNull()
                                        }
                                            ?: royaltyRepository.findById(it.address).awaitSingleOrNull(),
                                        attributeRepository.findByItem(it.address).collectList().awaitSingle(),
                                    )
                                }
                            }
                    }
            }
}
