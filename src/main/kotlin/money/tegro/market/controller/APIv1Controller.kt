package money.tegro.market.controller

import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.properties.MarketplaceProperties
import money.tegro.market.repository.ItemRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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

@RestController
class APIv1Controller(
    private val marketplaceProperties: MarketplaceProperties,
    private val itemRepository: ItemRepository,
) {
    @RequestMapping("/api/v1/transfer")
    fun transfer(
        @RequestParam(required = true) item: MsgAddressInt,
        @RequestParam(required = true) newOwner: MsgAddressInt,
        @RequestParam responseDestination: MsgAddressInt?,
    ) = TransactionRequestModel(
        dest = item,
        value = BigInt(100_000_000),
        stateInit = null,
        text = "NFT Item Transfer",
        payload = CellBuilder.createCell {
            storeTlb(
                ItemOp, TransferOp(
                    query_id = SecureRandom.nextULong(),
                    new_owner = newOwner,
                    response_destination = responseDestination ?: AddrNone,
                    custom_payload = Maybe.of(null),
                    forward_amount = VarUInteger(BigInt(50_000_000)),
                    forward_payload = Either.of(Cell.of(), null)
                )
            )
        }
    )

    @RequestMapping("/api/v1/sell")
    suspend fun sell(
        @RequestParam(required = true) item: MsgAddressInt,
        @RequestParam(required = true) seller: MsgAddressInt,
        price: BigInt,
    ): TransactionRequestModel {
        val royalty = itemRepository.getRoyalty(item)
        val marketplaceFee = price * marketplaceProperties.feeNumerator / marketplaceProperties.feeDenominator
        val royaltyValue = royalty?.let { price * it.numerator.toBigInt() / it.denominator.toBigInt() } ?: BigInt.ZERO
        val fullPrice = price + royaltyValue + marketplaceFee

        val payloadCell = CellBuilder.createCell {
            storeTlb(MsgAddress, marketplaceProperties.marketplaceAddress) // marketplace_address
            storeTlb(MsgAddress, item) // nft_address
            storeTlb(MsgAddress, seller) // nft_owner_address
            storeTlb(Coins.tlbCodec(), Coins.ofNano(fullPrice)) // full_price
            storeRef { // fees_cell
                storeTlb(Coins, Coins.ofNano(marketplaceFee))
                storeTlb(MsgAddress, royalty?.destination ?: AddrNone)
                storeTlb(Coins, Coins.ofNano(royaltyValue))
            }
        }

        return TransactionRequestModel(
            dest = item,
            value = marketplaceProperties.saleInitializationFee + marketplaceProperties.networkFee,
            stateInit = null,
            text = "NFT put up for sale",
            payload = CellBuilder.createCell {
                storeTlb(
                    ItemOp, TransferOp(
                        query_id = SecureRandom.nextULong(),
                        new_owner = marketplaceProperties.marketplaceAddress,
                        response_destination = seller,
                        custom_payload = Maybe.of(null),
                        forward_amount = VarUInteger(marketplaceProperties.saleInitializationFee),
                        forward_payload = Either.of(CellBuilder.createCell {
                            storeRef(payloadCell)
                            storeRef {
                                storeBytes(
                                    Ed25519.sign(
                                        marketplaceProperties.marketplaceAuthorizationPrivateKey,
                                        payloadCell.hash()
                                    )
                                )
                            }
                        }, null)
                    )
                )
            }
        )
    }
}
