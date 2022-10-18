package money.tegro.market.repository

import com.sksamuel.aedile.core.caffeineBuilder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.properties.CacheProperties
import money.tegro.market.properties.MarketplaceProperties
import money.tegro.market.service.ReferenceBlockService
import money.tegro.market.service.RoyaltyService
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Repository
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

@Repository
class ItemRepository(
    private val cacheProperties: CacheProperties,
    private val marketplaceProperties: MarketplaceProperties,
    private val liteClient: LiteClient,
    private val referenceBlockService: ReferenceBlockService,
    private val royaltyService: RoyaltyService,
    private val approvalRepository: ApprovalRepository,
    private val collectionRepository: CollectionRepository,
) {
    @OptIn(FlowPreview::class)
    fun listAll() =
        merge(
            // Collection items
            collectionRepository.listAll()
                .flatMapConcat {
                    collectionRepository.listCollectionItems(it).mapNotNull { it.second as? MsgAddressInt }
                },
            // Standalone items
            approvalRepository.findAllByApprovedIsTrue()
                .map { it.address }
                .filter { getContract(it) != null }
        )

    fun listItemsOwnedBy(owner: MsgAddress) =
        listAll()
            .filter { address ->
                getContract(address)?.owner == owner ||
                        getItemSale(address)?.owner == owner
            }

    private val contractCache =
        caffeineBuilder<MsgAddressInt, Optional<ItemContract>>().build()
    private val metadataCache =
        caffeineBuilder<MsgAddressInt, Optional<ItemMetadata>> {
            expireAfterWrite = cacheProperties.itemMetadataExpires
        }.build()
    private val saleCache =
        caffeineBuilder<MsgAddressInt, Optional<SaleContract>>().build()

    suspend fun getContract(item: MsgAddressInt): ItemContract? =
        contractCache.getOrPut(item) { address ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", StructuredArguments.kv("address", address.toRaw()))
                Optional.empty()
            } else {
                try {
                    logger.debug("fetching item {}", StructuredArguments.kv("address", address.toRaw()))
                    ItemContract.of(address as AddrStd, liteClient, referenceBlockService.get())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn(
                        "could not get item information for {}",
                        StructuredArguments.kv("address", address.toRaw()), e
                    )
                    Optional.empty()
                }
            }
        }
            .orElse(null)

    suspend fun getMetadata(item: MsgAddressInt): ItemMetadata? =
        metadataCache.getOrPut(item) { address ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", StructuredArguments.kv("address", address.toRaw()))
                Optional.empty()
            } else {
                getContract(address)
                    ?.let { contract ->
                        ItemMetadata.of(
                            (contract.collection as? AddrStd) // Collection items
                                ?.let {
                                    CollectionContract.itemContent(
                                        it,
                                        contract.index,
                                        contract.individual_content,
                                        liteClient,
                                        referenceBlockService.get(),
                                    )
                                }
                                ?: contract.individual_content) // Standalone items
                    }
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    suspend fun getSale(sale: MsgAddressInt): SaleContract? =
        saleCache.getOrPut(sale) { address ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", StructuredArguments.kv("address", address.toRaw()))
                Optional.empty()
            } else {
                logger.debug("fetching sale information {}", StructuredArguments.kv("address", address.toRaw()))
                SaleContract.of(address as AddrStd, liteClient, referenceBlockService.get())
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    suspend fun getItemSale(item: MsgAddressInt): SaleContract? =
        (getContract(item)?.owner as? MsgAddressInt)?.let { getSale(it) }

    suspend fun getRoyalty(item: MsgAddressInt): RoyaltyContract? =
        (getContract(item)?.collection as? MsgAddressInt)?.let { royaltyService.get(it) } // Collection item
            ?: royaltyService.get(item) // Standalone item


    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.item",
                ),
                exchange = Exchange(
                    name = "blocks",
                    type = ExchangeTypes.TOPIC,
                ),
                key = ["live"], // Only live blocks
            )
        ]
    )
    fun onLiveBlock(block: Block) {
        block.accountBlockAddresses()
            .forEach {
                contractCache.underlying().synchronous().invalidate(it as MsgAddressInt)
                saleCache.underlying().synchronous().invalidate(it as MsgAddressInt)
            }
    }

    companion object : KLogging()
}
