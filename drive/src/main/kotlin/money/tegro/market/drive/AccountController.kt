package money.tegro.market.drive

import io.micronaut.http.annotation.Controller
import money.tegro.market.core.dto.AccountDTO
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.mapper.AccountMapper
import money.tegro.market.core.mapper.CollectionMapper
import money.tegro.market.core.mapper.ItemMapper
import money.tegro.market.core.operations.AccountOperations
import money.tegro.market.core.repository.*
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/accounts")
class AccountController(
    private val accountRepository: AccountRepository,
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val saleRepository: SaleRepository,
    private val attributeRepository: AttributeRepository,
    private val royaltyRepository: RoyaltyRepository,

    private val accountMapper: AccountMapper,
    private val collectionMapper: CollectionMapper,
    private val itemMapper: ItemMapper,
) : AccountOperations {
    override fun getAccount(account: String): Mono<AccountDTO> =
        accountRepository.findById(AddrStd(account)).flatMap(accountMapper::map)

    override fun getAccountItems(account: String): Flux<ItemDTO> =
        saleRepository.findByOwner(AddrStd(account))
            .flatMap { (it.item as? AddrStd)?.let { itemRepository.findById(it) } } // Items on sale (account -> sale -> item)
            .concatWith { itemRepository.findByOwner(AddrStd(account)) } // Items not on sale
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

    override fun getAccountCollections(account: String): Flux<CollectionDTO> =
        collectionRepository.findByOwner(AddrStd(account))
            .flatMap {
                collectionMapper.map(
                    collection = it,
                    itemNumber = itemRepository.countByCollection(it.address),
                    royalty = royaltyRepository.findById(it.address)
                )
            }

}
