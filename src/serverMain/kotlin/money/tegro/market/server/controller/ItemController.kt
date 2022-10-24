package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.resource.ItemResource
import money.tegro.market.server.properties.MarketplaceProperties
import money.tegro.market.server.repository.ItemRepository
import money.tegro.market.server.toBigInteger
import money.tegro.market.toBase64
import money.tegro.market.toRaw
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.ton.bigint.BigInt
import org.ton.bigint.toBigInt
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.SecureRandom
import org.ton.crypto.ed25519.Ed25519
import org.ton.tlb.storeTlb
import kotlin.random.nextULong

class ItemController(application: Application) : AbstractDIController(application) {
    private val itemRepository: ItemRepository by instance()
    private val marketplaceProperties: MarketplaceProperties by instance()

    override fun Route.getRoutes() {
        get<ItemResource> { request ->
            requireNotNull(itemRepository.get(MsgAddressInt(request.address)))
                .let { call.respond(it) }
        }

        get<ItemResource.Transfer> { request ->
            TransactionRequestModel(
                dest = MsgAddressInt(request.parent.address).toRaw(),
                value = (marketplaceProperties.transferFee.amount.value + marketplaceProperties.networkFee.amount.value).toBigInteger(),
                stateInit = null,
                text = "NFT Item Transfer",
                payload = CellBuilder.createCell {
                    storeTlb(
                        ItemOp, TransferOp(
                            query_id = SecureRandom.nextULong(),
                            new_owner = MsgAddressInt(request.newOwner),
                            response_destination = request.response?.let { MsgAddressInt(it) } ?: AddrNone,
                            custom_payload = Maybe.of(null),
                            forward_amount = marketplaceProperties.transferFee.amount,
                            forward_payload = Either.of(Cell.of(), null)
                        )
                    )
                }
                    .toBase64()
            )
                .let { call.respond(it) }
        }

        get<ItemResource.Sell> { request ->
            val itemAddress = MsgAddressInt(request.parent.address)
            val sellerAddress = MsgAddressInt(request.seller)
            val priceBigInt = BigInt(request.price) // TODO

            val royalty = itemRepository.getRoyalty(itemAddress)
            val marketplaceFee =
                priceBigInt * marketplaceProperties.serviceFeeNumerator.toBigInt() / marketplaceProperties.serviceFeeDenominator.toBigInt()
            val royaltyValue =
                royalty?.let { priceBigInt * it.numerator.toBigInt() / it.denominator.toBigInt() } ?: BigInt.ZERO
            val fullPrice = priceBigInt + royaltyValue + marketplaceFee

            val payloadCell = CellBuilder.createCell {
                storeTlb(MsgAddress, marketplaceProperties.address) // marketplace_address
                storeTlb(MsgAddress, itemAddress) // nft_address
                storeTlb(MsgAddress, sellerAddress) // nft_owner_address
                storeTlb(Coins.tlbCodec(), Coins.ofNano(fullPrice)) // full_price
                storeRef { // fees_cell
                    storeTlb(Coins, Coins.ofNano(marketplaceFee))
                    storeTlb(MsgAddress, royalty?.destination ?: AddrNone)
                    storeTlb(Coins, Coins.ofNano(royaltyValue))
                }
            }

            TransactionRequestModel(
                dest = itemAddress.toRaw(),
                value = (marketplaceProperties.saleFee.amount.value
                        + marketplaceProperties.transferFee.amount.value
                        + marketplaceProperties.networkFee.amount.value).toBigInteger(),
                stateInit = null,
                text = "NFT put up for sale",
                payload = CellBuilder.createCell {
                    storeTlb(
                        ItemOp, TransferOp(
                            query_id = SecureRandom.nextULong(),
                            new_owner = marketplaceProperties.address,
                            response_destination = sellerAddress,
                            custom_payload = Maybe.of(null),
                            forward_amount = marketplaceProperties.saleFee.amount,
                            forward_payload = Either.of(CellBuilder.createCell {
                                storeRef(payloadCell)
                                storeRef {
                                    storeBytes(
                                        Ed25519.sign(
                                            marketplaceProperties.authorizationPrivateKey,
                                            payloadCell.hash()
                                        )
                                    )
                                }
                            }, null)
                        )
                    )
                }.toBase64()
            )
                .let { call.respond(it) }
        }
    }
}
