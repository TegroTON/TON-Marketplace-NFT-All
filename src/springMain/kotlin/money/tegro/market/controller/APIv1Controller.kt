package money.tegro.market.controller

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.dropTake
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ImageDTO
import money.tegro.market.dto.ItemDTO
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.operations.APIv1Operations
import money.tegro.market.properties.MarketplaceProperties
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.repository.ItemRepository
import money.tegro.market.toBase64
import money.tegro.market.toBigInteger
import money.tegro.market.toRaw
import mu.KLogging
import org.springframework.web.bind.annotation.PathVariable
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
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : APIv1Operations {
    @RequestMapping("/api/v1/collections/top")
    override fun listTopCollections(
        @RequestParam drop: Int?,
        @RequestParam take: Int?,
    ): Flow<CollectionDTO> =
        collectionRepository.listAll()
            .mapNotNull { address ->
                try {
                    getCollection(address.toRaw())
                } catch (_: Exception) {
                    null
                }
            }
            .dropTake(drop, take)

    @RequestMapping("/api/v1/collection/{collection}")
    override suspend fun getCollection(@PathVariable collection: String): CollectionDTO {
        val address = MsgAddressInt(collection)
        val contract = requireNotNull(collectionRepository.getContract(address))
        val metadata = collectionRepository.getMetadata(address)

        return CollectionDTO(
            address = address.toRaw(),
            numberOfItems = contract.next_item_index,
            owner = contract.owner.toRaw(),
            name = metadata?.name ?: "Untitled Collection",
            description = metadata?.description.orEmpty(),
            image = ImageDTO(
                original = metadata?.image
            ),
            coverImage = ImageDTO(
                original = metadata?.cover_image ?: metadata?.image
            ),
        )
    }

    @RequestMapping("/api/v1/collection/{collection}/items")
    override fun listCollectionItems(
        @PathVariable collection: String,
        @RequestParam drop: Int?,
        @RequestParam take: Int?
    ): Flow<ItemDTO> =
        collectionRepository.listCollectionItems(MsgAddressInt(collection))
            .mapNotNull { (_, address) ->
                try {
                    address?.toRaw()?.let { getItem(it) }
                } catch (_: Exception) {
                    null
                }
            }
            .dropTake(drop, take)

    @RequestMapping("/api/v1/item/{item}")
    override suspend fun getItem(@PathVariable item: String): ItemDTO {
        val address = MsgAddressInt(item)
        val contract = requireNotNull(itemRepository.getContract(address))
        val metadata = itemRepository.getMetadata(address)
        val sale = itemRepository.getItemSale(address)
        val royalty = itemRepository.getRoyalty(address)

        return ItemDTO(
            address = address.toRaw(),
            index = contract.index,
            collection = contract.collection.toRaw(),
            owner = (sale?.owner ?: contract.owner).toRaw(),
            name = metadata?.name ?: "Item no. ${contract.index}",
            description = metadata?.description.orEmpty(),
            image = ImageDTO(
                original = metadata?.image
            ),
            attributes = metadata?.attributes.orEmpty().associate { it.trait to it.value },

            sale = (if (sale != null) contract.owner else null)?.toRaw(),
            marketplace = sale?.marketplace?.toRaw(),
            fullPrice = sale?.full_price?.toBigInteger(),
            marketplaceFee = sale?.marketplace_fee?.toBigInteger(),
            royalties = sale?.royalty?.toBigInteger(),
            royaltyDestination = sale?.royalty_destination?.toRaw(),
            royaltyPercentage = BigDecimal.fromInt(royalty?.numerator ?: 0)
                    / BigDecimal.fromInt(royalty?.denominator ?: 1),
            marketplaceFeePercentage = BigDecimal.fromInt(marketplaceProperties.marketplaceFeeNumerator)
                    / BigDecimal.fromInt(marketplaceProperties.marketplaceFeeDenominator),

            saleInitializationFee = marketplaceProperties.saleInitializationFee.amount.value.toBigInteger(),
            transferFee = marketplaceProperties.itemTransferFee.amount.value.toBigInteger(),
            networkFee = marketplaceProperties.networkFee.amount.value.toBigInteger(),
            minimalGasFee = marketplaceProperties.minimalGasFee.amount.value.toBigInteger(),
        )
    }

    @RequestMapping("/api/v1/item/{item}/transfer")
    override suspend fun transferItem(
        @PathVariable item: String,
        @RequestParam(required = true) newOwner: String,
        @RequestParam responseDestination: String?,
    ) = TransactionRequestDTO(
        dest = MsgAddressInt(item).toRaw(),
        value = (marketplaceProperties.itemTransferFee + marketplaceProperties.networkFee).amount.value.toBigInteger(),
        stateInit = null,
        text = "NFT Item Transfer",
        payload = CellBuilder.createCell {
            storeTlb(
                ItemOp, TransferOp(
                    query_id = SecureRandom.nextULong(),
                    new_owner = MsgAddressInt(newOwner),
                    response_destination = responseDestination?.let { MsgAddressInt(it) } ?: AddrNone,
                    custom_payload = Maybe.of(null),
                    forward_amount = marketplaceProperties.itemTransferFee.amount,
                    forward_payload = Either.of(Cell.of(), null)
                )
            )
        }
            .toBase64()
    )

    @RequestMapping("/api/v1/item/{item}/sell")
    override suspend fun sellItem(
        @PathVariable item: String,
        @RequestParam(required = true) seller: String,
        price: BigInteger,
    ): TransactionRequestDTO {
        val itemAddress = MsgAddressInt(item)
        val sellerAddress = MsgAddressInt(seller)
        val priceBigInt = BigInt(price.toString())

        val royalty = itemRepository.getRoyalty(itemAddress)
        val marketplaceFee =
            priceBigInt * marketplaceProperties.marketplaceFeeNumerator.toBigInt() / marketplaceProperties.marketplaceFeeDenominator.toBigInt()
        val royaltyValue =
            royalty?.let { priceBigInt * it.numerator.toBigInt() / it.denominator.toBigInt() } ?: BigInt.ZERO
        val fullPrice = priceBigInt + royaltyValue + marketplaceFee

        val payloadCell = CellBuilder.createCell {
            storeTlb(MsgAddress, marketplaceProperties.marketplaceAddress) // marketplace_address
            storeTlb(MsgAddress, itemAddress) // nft_address
            storeTlb(MsgAddress, sellerAddress) // nft_owner_address
            storeTlb(Coins.tlbCodec(), Coins.ofNano(fullPrice)) // full_price
            storeRef { // fees_cell
                storeTlb(Coins, Coins.ofNano(marketplaceFee))
                storeTlb(MsgAddress, royalty?.destination ?: AddrNone)
                storeTlb(Coins, Coins.ofNano(royaltyValue))
            }
        }

        return TransactionRequestDTO(
            dest = item,
            value = (marketplaceProperties.saleInitializationFee
                    + marketplaceProperties.itemTransferFee
                    + marketplaceProperties.networkFee).amount.value.toBigInteger(),
            stateInit = null,
            text = "NFT put up for sale",
            payload = CellBuilder.createCell {
                storeTlb(
                    ItemOp, TransferOp(
                        query_id = SecureRandom.nextULong(),
                        new_owner = marketplaceProperties.marketplaceAddress,
                        response_destination = sellerAddress,
                        custom_payload = Maybe.of(null),
                        forward_amount = marketplaceProperties.saleInitializationFee.amount,
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
            }.toBase64()
        )
    }

    companion object : KLogging()
}
