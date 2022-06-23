package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.dto.TransactionRequestDTO
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.operations.ItemOperations
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.Coins
import org.ton.block.Either
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressIntStd
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.storeTlb
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Controller("/items")
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

    override fun transferItem(
        item: String,
        from: String,
        to: String,
    ): Mono<TransactionRequestDTO> = mono {
        TransactionRequestDTO(
            to = MsgAddressIntStd(item).toSafeBounceable(),
            value = 100_000_000, // 0.1 TON
            stateInit = null,
            payload = CellBuilder.createCell {
                storeUInt(0x5fcc3d14, 32) // OP, transfer
                storeUInt(0, 64) // Query id
                storeTlb(MsgAddress.tlbCodec(), MsgAddressIntStd(to)) // new owner
                storeTlb(MsgAddress.tlbCodec(), MsgAddressIntStd(from)) // response destination
                // in_msg_body~load_int(1); ;; this nft don't use custom_payload
                // bruh moment
                storeInt(0, 1)
                storeTlb(Coins.tlbCodec(), Coins.ofNano(6_900_000)) // 0.0069 because funny, forward amount
                storeTlb(
                    Either.tlbCodec(Cell.tlbCodec(), Cell.tlbCodec()),
                    Either.of(Cell.of(), null)
                )
            }.bits.toByteArray().let { base64(it) }
        )
    }
}
