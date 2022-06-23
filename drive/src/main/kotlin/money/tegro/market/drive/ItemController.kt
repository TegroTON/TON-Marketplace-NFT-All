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
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressExtNone
import org.ton.block.MsgAddressIntStd
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
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

    override fun getItem(item: String) =
        itemRepository.findByAddressStd(MsgAddressIntStd(item))
            .map { ItemDTO(it, it.collection?.let { collectionRepository.findById(it).block() }) }

    override fun transferItem(
        item: String,
        to: String,
        response: String?,
    ): Mono<TransactionRequestDTO> = mono {
        TransactionRequestDTO(
            to = MsgAddressIntStd(item).toSafeBounceable(),
            value = 100_000_000, // 0.1 TON
            stateInit = null,
            payload = CellBuilder.createCell {
                storeUInt(0x5fcc3d14, 32) // OP, transfer
                storeUInt(0, 64) // Query id
                storeTlb(MsgAddress.tlbCodec(), MsgAddressIntStd(to)) // new owner
                storeTlb(
                    MsgAddress.tlbCodec(),
                    response?.let { MsgAddressIntStd(it) }
                        ?: MsgAddressExtNone) // response destination. Rest of coins is sent there
                storeInt(0, 1) // custom_data, unused
                storeTlb(Coins.tlbCodec(), Coins.ofNano(6_900_000)) // 0.0069 because funny, forward amount
                // Extra payload here, unused in this case
            }.let { BagOfCells(it) }.toByteArray().let { base64(it) }
        )
    }
}
