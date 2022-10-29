package money.tegro.market.server.repository

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.github.reactivecircus.cache4k.Cache
import io.ktor.client.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.model.ImageModel
import money.tegro.market.model.ItemModel
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.SaleItemModel
import money.tegro.market.server.properties.MarketplaceProperties
import money.tegro.market.server.service.ReferenceBlockService
import money.tegro.market.server.toBigInteger
import money.tegro.market.toRaw
import mu.KLogging
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

class ItemRepository(override val di: DI) : DIAware {
    private val liteClient: LiteClient by instance()
    private val httpClient: HttpClient by instance()

    private val approvalRepository: ApprovalRepository by instance()
    private val collectionRepository: CollectionRepository by instance()
    private val royaltyRepository: RoyaltyRepository by instance()

    private val marketplaceProperties: MarketplaceProperties by instance()

    private val contractCache: Cache<MsgAddressInt, Optional<ItemContract>> by instance()
    private val metadataCache: Cache<MsgAddressInt, Optional<ItemMetadata>> by instance()
    private val saleCache: Cache<MsgAddressInt, Optional<SaleContract>> by instance()

    private val referenceBlockService: ReferenceBlockService by instance()

    @OptIn(FlowPreview::class)
    fun all() =
        merge(
            // Collection items
            collectionRepository.all()
                .flatMapConcat {
                    collectionRepository.itemsOf(MsgAddressInt(it.address)).mapNotNull { it.second as? MsgAddressInt }
                },
            // Standalone items
            approvalRepository.allApproved()
                .asFlow()
                .map { it.address }
        )
            .mapNotNull { get(it) }

    suspend fun get(item: MsgAddressInt): ItemModel? =
        getContract(item)?.let { contract ->
            getMetadata(item)?.let { metadata ->
                val sale = itemSale(item)
                if (sale != null) {
                    SaleItemModel(
                        address = item.toRaw(),
                        index = contract.index,
                        collection = (contract.collection as? MsgAddressInt)?.let { collectionRepository.get(it) },
                        owner = sale.owner.toRaw(),
                        name = metadata.name ?: "Item no. ${contract.index}",
                        description = metadata.description.orEmpty(),
                        image = ImageModel(
                            original = metadata.image
                        ),
                        attributes = metadata.attributes.orEmpty().associate { it.trait to it.value },
                        sale = (contract.owner as MsgAddressInt).toRaw(),
                        marketplace = sale.marketplace.toRaw(),
                        fullPrice = sale.full_price.toBigInteger(),
                        marketplaceFee = sale.marketplace_fee.toBigInteger(),
                        royalties = sale.royalty.toBigInteger(),
                        royaltyDestination = sale.royalty_destination.toRaw(),
                        networkFee = marketplaceProperties.gasFee.amount.value.toBigInteger(),
                    )
                } else {
                    val royalty = getRoyalty(item)
                    OrdinaryItemModel(
                        address = item.toRaw(),
                        index = contract.index,
                        collection = (contract.collection as? MsgAddressInt)?.let { collectionRepository.get(it) },
                        owner = contract.owner.toRaw(),
                        name = metadata.name ?: "Item no. ${contract.index}",
                        description = metadata.description.orEmpty(),
                        image = ImageModel(
                            original = metadata.image
                        ),
                        attributes = metadata.attributes.orEmpty().associate { it.trait to it.value },
                        royaltyValue = BigDecimal.fromInt(royalty?.numerator ?: 0)
                            .divide(BigDecimal.fromInt(royalty?.denominator ?: 1)),
                        marketplaceFeeValue = BigDecimal.fromInt(marketplaceProperties.serviceFeeNumerator)
                            .divide(BigDecimal.fromInt(marketplaceProperties.serviceFeeDenominator)),
                        saleInitializationFee = marketplaceProperties.saleFee.amount.value.toBigInteger(),
                        transferFee = marketplaceProperties.transferFee.amount.value.toBigInteger(),
                        networkFee = marketplaceProperties.networkFee.amount.value.toBigInteger(),
                    )
                }
            }
        }

    fun byOwner(owner: MsgAddress) =
        all()
            .filter { it.owner == owner.toRaw() }


    fun byCollection(collection: MsgAddressInt) =
        collectionRepository.itemsOf(collection).mapNotNull { it.second as? MsgAddressInt }
            .mapNotNull { get(it) }

    private suspend fun getContract(item: MsgAddressInt): ItemContract? =
        contractCache.get(item) {
            if (approvalRepository.isDisapproved(item)) { // Explicitly forbidden
                logger.debug { "address=${item.toRaw()} was disapproved" }
                Optional.empty()
            } else {
                try {
                    logger.debug { "fetching item address=${item.toRaw()}" }
                    ItemContract.of(item as AddrStd, liteClient, referenceBlockService.last())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn(e) { "could not get item information for address=${item.toRaw()}" }
                    Optional.empty()
                }
            }
        }
            .orElse(null)

    private suspend fun getMetadata(item: MsgAddressInt): ItemMetadata? =
        metadataCache.get(item) {
            if (approvalRepository.isDisapproved(item)) { // Explicitly forbidden
                logger.debug { "address=${item.toRaw()} was disapproved" }
                Optional.empty()
            } else {
                getContract(item)
                    ?.let { contract ->
                        ItemMetadata.of(
                            (contract.collection as? AddrStd) // Collection items
                                ?.let {
                                    CollectionContract.itemContent(
                                        it,
                                        contract.index,
                                        contract.individual_content,
                                        liteClient,
                                        referenceBlockService.last()
                                    )
                                }
                                ?: contract.individual_content, // Standalone items
                            httpClient
                        )
                    }
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    private suspend fun getSale(sale: MsgAddressInt): SaleContract? =
        saleCache.get(sale) {
            if (approvalRepository.isDisapproved(sale)) { // Explicitly forbidden
                logger.debug { "address=${sale.toRaw()} was disapproved" }
                Optional.empty()
            } else {
                logger.debug { "fetching sale address=${sale.toRaw()}" }
                SaleContract.of(sale as AddrStd, liteClient, referenceBlockService.last())
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    private suspend fun itemSale(item: MsgAddressInt): SaleContract? =
        (getContract(item)?.owner as? MsgAddressInt)?.let { getSale(it) }

    suspend fun getRoyalty(item: MsgAddressInt): RoyaltyContract? =
        (getContract(item)?.collection as? MsgAddressInt)?.let { royaltyRepository.get(it) } // Collection item
            ?: royaltyRepository.get(item) // Standalone item

    companion object : KLogging()
}
