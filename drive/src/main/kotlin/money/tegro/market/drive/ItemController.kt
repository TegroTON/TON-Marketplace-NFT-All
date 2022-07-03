package money.tegro.market.drive

import io.micronaut.data.model.Pageable
import io.micronaut.http.annotation.Controller
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.configuration.MarketplaceConfiguration
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.dto.TransactionRequestDTO
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.operations.ItemOperations
import money.tegro.market.core.repository.*
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.Coins
import org.ton.block.MsgAddress
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.Ed25519
import org.ton.crypto.base64
import org.ton.tlb.storeTlb
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/items")
class ItemController(
    private val configuration: MarketplaceConfiguration,

    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val attributeRepository: AttributeRepository,
    private val saleRepository: SaleRepository,
    private val royaltyRepository: RoyaltyRepository,
) : ItemOperations {
    override fun getAll(pageable: Pageable): Flux<ItemDTO> =
        itemRepository.findAll(pageable)
            .flatMapMany {
                it.toFlux().flatMap {
                    mono {
                        ItemDTO(
                            it,
                            saleRepository.findByItem(it.address).awaitSingleOrNull(),
                            it.collection?.let { royaltyRepository.findById(it).awaitSingleOrNull() }
                                ?: royaltyRepository.findById(it.address).awaitSingleOrNull(),
                            attributeRepository.findByItem(it.address).collectList().awaitSingle(),
                        )
                    }
                }
            }

    override fun getItem(item: String): Mono<ItemDTO> =
        itemRepository.findById(AddrStd(item))
            .flatMap {
                mono {
                    ItemDTO(
                        it,
                        saleRepository.findByItem(it.address).awaitSingleOrNull(),
                        it.collection?.let { royaltyRepository.findById(it).awaitSingleOrNull() }
                            ?: royaltyRepository.findById(it.address).awaitSingleOrNull(),
                        attributeRepository.findByItem(it.address).collectList().awaitSingle(),
                    )
                }
            }

    override fun transferItem(
        item: String,
        to: String,
        response: String?,
    ): Mono<TransactionRequestDTO> = mono {
        TransactionRequestDTO(
            to = AddrStd(item).toSafeBounceable(),
            value = configuration.itemTransferAmount, // 0.1 TON
            stateInit = null,
            payload = CellBuilder.createCell {
                storeUInt(0x5fcc3d14, 32) // OP, transfer
                storeUInt(0, 64) // Query id
                storeTlb(MsgAddress.tlbCodec(), AddrStd(to)) // new owner
                storeTlb(
                    MsgAddress.tlbCodec(),
                    response?.let { AddrStd(it) }
                        ?: AddrNone) // response destination. Rest of coins is sent there
                storeInt(0, 1) // custom_data, unused
                storeTlb(Coins.tlbCodec(), Coins.ofNano(configuration.itemTransferAmount / 2))
                // Extra payload here, unused in this case
            }.let { BagOfCells(it) }.toByteArray().let { base64(it) }
        )
    }

    override fun sellItem(
        item: String,
        from: String,
        price: Long,
    ): Mono<TransactionRequestDTO> = mono {
        val it = itemRepository.findById(AddrStd(item)).awaitSingle()
        val royalty = it.collection?.let { royaltyRepository.findById(it).awaitSingleOrNull() }
            ?: royaltyRepository.findById(it.address).awaitSingleOrNull()

        val marketplaceFee = price * configuration.feeNumerator / configuration.feeDenominator
        val royaltyValue = royalty?.let { price * it.numerator / it.denominator } ?: 0L
        val fullPrice = price + marketplaceFee + royaltyValue

        val payloadCell = CellBuilder.createCell {
            storeTlb(MsgAddress.tlbCodec(), configuration.marketplaceAddress) // marketplace_address
            storeTlb(MsgAddress.tlbCodec(), AddrStd(item)) // nft_address
            storeTlb(MsgAddress.tlbCodec(), AddrStd(from)) // nft_owner_address
            storeTlb(Coins.tlbCodec(), Coins.ofNano(fullPrice)) // full_price
            storeRef { // fees_cell
                storeTlb(Coins.tlbCodec(), Coins.ofNano(marketplaceFee))
                storeTlb(MsgAddress.tlbCodec(), royalty?.destination?.to() ?: AddrNone)
                storeTlb(Coins.tlbCodec(), Coins.ofNano(royaltyValue))
            }
        }

        TransactionRequestDTO(
            to = AddrStd(item).toSafeBounceable(),
            value = configuration.saleInitializationFee + configuration.blockchainFee,
            stateInit = null,
            payload = CellBuilder.createCell {
                storeUInt(0x5fcc3d14, 32) // OP, transfer
                storeUInt(0, 64) // Query id
                storeTlb(
                    MsgAddress.tlbCodec(),
                    configuration.marketplaceAddress
                ) // new owner, item is transferred to the marketplace
                storeTlb(
                    MsgAddress.tlbCodec(),
                    AddrStd(from)
                ) // response destination. Rest of coins is sent back
                storeInt(0, 1) // custom_data, unused
                storeTlb(Coins.tlbCodec(), Coins.ofNano(configuration.saleInitializationFee))
                // Extra payload here, used by the market contract to do its magic
                storeRef(payloadCell)
                storeRef {
                    // Signature to make sure that the message came from our server and wasn't messed with by someone else
                    storeBytes(Ed25519.sign(configuration.marketplaceAuthorizationPrivateKey, payloadCell.hash()))
                }
            }.let { BagOfCells(it) }.toByteArray().let { base64(it) }
        )
    }
}
