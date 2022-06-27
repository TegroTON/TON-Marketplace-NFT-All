package money.tegro.market.drive

import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.AccountDTO
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.operations.AccountOperations
import money.tegro.market.core.repository.*
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller("/accounts")
class AccountController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val saleRepository: SaleRepository,
    private val attributeRepository: AttributeRepository,
) : AccountOperations {
    override fun getAccount(account: String): Mono<AccountDTO> = mono {
        AccountDTO(AddrStd(account).toSafeBounceable())
    }

    override fun getAccountItems(account: String): Flux<ItemDTO> =
        saleRepository.findByOwnerStd(AddrStd(account))
            .flatMap {
                itemRepository.findByOwner(it.address).take(1)
            } // Items owned by the seller contract owned by the account. We only expect 1 item = 1 sale
            .concatWith(itemRepository.findByOwnerStd(AddrStd(account))) // Items not on sale            .
            .flatMap {
                mono {
                    ItemDTO(
                        it,
                        it.collection?.let { collectionRepository.findById(it).awaitSingle() },
                        attributeRepository.findByItem(it.address).collectList().awaitSingle(),
                        saleRepository.findByItem(it.address).awaitSingleOrNull(),
                    )
                }
            }

    override fun getAccountCollections(account: String): Flux<CollectionDTO> =
        collectionRepository.findByOwnerStd(AddrStd(account))
            .flatMap { mono { CollectionDTO(it, itemRepository.countByCollection(it.address)) } }
}
