package money.tegro.market.repository

import com.sksamuel.aedile.core.caffeineBuilder
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.properties.CacheProperties
import money.tegro.market.service.ReferenceBlockService
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
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
class CollectionRepository(
    private val cacheProperties: CacheProperties,
    private val liteClient: LiteClient,
    private val referenceBlockService: ReferenceBlockService,
    private val approvalRepository: ApprovalRepository,
) {
    fun listAll() =
        approvalRepository.findAllByApprovedIsTrue()
            .map { it.address }
            .filter { getContract(it) != null }

    private val contractCache =
        caffeineBuilder<MsgAddressInt, Optional<CollectionContract>>().build()
    private val metadataCache =
        caffeineBuilder<MsgAddressInt, Optional<CollectionMetadata>> {
            expireAfterWrite = cacheProperties.collectionMetadataExpires
        }.build()
    private val itemAddressCache =
        caffeineBuilder<Pair<MsgAddressInt, ULong>, Optional<MsgAddress>>().build()

    suspend fun getContract(address: MsgAddressInt): CollectionContract? =
        contractCache.getOrPut(address) { collection ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                try {
                    logger.debug("fetching collection {}", kv("address", collection.toRaw()))
                    CollectionContract.of(collection as AddrStd, liteClient, referenceBlockService.get())
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn(
                        "could not get collection information for {}",
                        kv("address", collection.toRaw()), e
                    )
                    Optional.empty()
                }
            } else {
                logger.warn("{} was not approved", kv("address", collection.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    suspend fun getMetadata(address: MsgAddressInt): CollectionMetadata? =
        metadataCache.getOrPut(address) { collection ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                getContract(collection)
                    ?.let {
                        CollectionMetadata.of(it.content)
                    }
                    .let { Optional.ofNullable(it) }
            } else {
                logger.warn("{} was not approved", kv("address", collection.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    suspend fun getItemAddress(
        collection: MsgAddressInt,
        index: ULong,
    ): MsgAddress? =
        itemAddressCache.getOrPut(collection to index) { (collection, index) ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                try {
                    CollectionContract.itemAddressOf(
                        collection as AddrStd,
                        index,
                        liteClient,
                        referenceBlockService.get()
                    )
                        .let { Optional.of(it) }
                } catch (e: TvmException) {
                    logger.warn(
                        "could not get item {} address of {}",
                        kv("index", index.toString()),
                        kv("collection", collection.toRaw())
                    )
                    Optional.empty()
                }
            } else {
                logger.warn("{} was not approved", kv("address", collection.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    fun listCollectionItems(address: MsgAddressInt) =
        flow {
            for (index in 0uL until (getContract(address)?.next_item_index ?: 0uL)) {
                emit(index)
            }
        }
            .mapNotNull { it to getItemAddress(address, it) }


    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.collection",
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
                val s = itemAddressCache.underlying().synchronous()
                s.asMap().keys
                    .filter { it.first == it }
                    .forEach {
                        s.invalidate(it)
                    }
            }
    }

    companion object : KLogging()
}
