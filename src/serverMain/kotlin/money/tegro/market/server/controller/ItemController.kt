package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.model.SaleItemModel
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.resource.ItemResource
import money.tegro.market.server.dropTake
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
            call.respond(
                when (request.sort) {
                    ItemResource.Sort.ALL -> itemRepository.all()
                        .dropTake(request.drop, request.take)
                        .toList()
                }
            )
        }

        get<ItemResource.ByAddress> { request ->
            call.respond(requireNotNull(itemRepository.get(MsgAddressInt(request.address))))
        }

        get<ItemResource.ByRelation> { request ->
            call.respond(
                when (request.relation) {
                    ItemResource.ByRelation.Relation.COLLECTION -> itemRepository.byCollection(MsgAddressInt(request.address))
                    ItemResource.ByRelation.Relation.OWNED -> itemRepository.byOwner(MsgAddressInt(request.address))
                }
                    .toList() // Damn
                    .let {
                        when (request.sortItems) {
                            ItemResource.ByRelation.Sort.INDEX -> it
                                .sortedBy { it.index }

                            ItemResource.ByRelation.Sort.PRICE -> it
                                .sortedBy { (it as? SaleItemModel)?.fullPrice }

                            else -> it
                        }
                    }
                    .let { if (request.sortReverse == true) it.reversed() else it }
                    .asFlow()
                    .dropTake(request.drop, request.take)
                    .toList()
            )
        }

        get<ItemResource.ByRelation.Attributes> { request ->
            call.respond(
                when (request.parent.relation) {
                    ItemResource.ByRelation.Relation.COLLECTION -> itemRepository.byCollection(MsgAddressInt(request.parent.address))
                    ItemResource.ByRelation.Relation.OWNED -> itemRepository.byOwner(MsgAddressInt(request.parent.address))
                }
                    .map { it.attributes }
                    .toList()
                    .flatMap { it.asSequence() }
                    .groupBy({ it.key }, { it.value })
                    .mapValues { (_, values) -> values.toSet() }
            )
        }

        get<ItemResource.ByAddress.Transfer> { request ->
            call.respond(
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
            )
        }

        get<ItemResource.ByAddress.Sell> { request ->
            val itemAddress = MsgAddressInt(request.parent.address)
            val sellerAddress = MsgAddressInt(request.seller)
            val priceBigInt = BigInt(request.price)

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

            call.respond(
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
            )
        }
    }
}
