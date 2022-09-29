package money.tegro.market.repository

import com.sksamuel.aedile.core.caffeineBuilder
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.model.CollectionModel
import money.tegro.market.model.ItemModel
import money.tegro.market.service.ReferenceBlockService
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
    private val liteClient: LiteClient,
    private val referenceBlockService: ReferenceBlockService,
    private val approvalRepository: ApprovalRepository,
    private val collectionRepository: CollectionRepository,
) {
    suspend fun getByAddress(address: MsgAddressInt): ItemModel =
        ItemModel.of(
            address,
            getContract(address),
            getMetadata(address),
            getItemSale(address),
        )

    suspend fun listCollectionItems(collection: MsgAddressInt) =
        collectionRepository.listItemAddresses(collection)
            .mapNotNull { addr -> (addr as? MsgAddressInt)?.let { getByAddress(it) } }

    suspend fun getItemCollection(item: MsgAddressInt): CollectionModel? =
        (getContract(item)?.collection as? MsgAddressInt)?.let { collectionRepository.getByAddress(it) }

    @OptIn(FlowPreview::class)
    fun listAll() =
        merge(
            // Collection items
            collectionRepository.listAll()
                .flatMapConcat { listCollectionItems(it.address) },
            // Standalone items
            approvalRepository.findAllByApprovedIsTrue()
                .map { it.address }
                .filter { getContract(it) != null }
                .map { getByAddress(it) }
        )

    fun listItemsOwnedBy(owner: MsgAddress) =
        listAll()
            .filter { it.owner == owner }

    private val contractCache =
        caffeineBuilder<MsgAddressInt, Optional<ItemContract>>().build()
    private val metadataCache =
        caffeineBuilder<MsgAddressInt, Optional<ItemMetadata>> {
            // TODO: Configuration
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
