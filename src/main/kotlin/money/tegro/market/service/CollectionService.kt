package money.tegro.market.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class CollectionService(
    private val cacheManager: CacheManager,
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    fun listAll() =
        approvalRepository.findAllByApprovedIsTrue()
            .map { it.address }
            .filter { getContract(it) != null }

    suspend fun getContract(
        address: MsgAddressInt,
        referenceBlock: suspend () -> TonNodeBlockIdExt? = { liteClient.getLastBlockId() }
    ): CollectionContract? {
        val cachedValue = contractCache()?.get(address)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {}", kv("address", address.toRaw()))
            cachedValue.get() as? CollectionContract
        } else {
            if (approvalRepository.existsByApprovedIsTrueAndAddress(address)) { // Has been explicitly approved
                try {
                    logger.debug("fetching collection {}", kv("address", address.toRaw()))
                    CollectionContract.of(address as AddrStd, liteClient, referenceBlock())
                } catch (e: TvmException) {
                    logger.warn("could not get collection information for {}", kv("address", address.toRaw()), e)
                    null
                }
            } else {
                logger.warn("{} was not approved", kv("address", address.toRaw()))
                null
            }
                .also { contract -> // Even if the result is null, cache it
                    contractCache()?.put(address, contract)
                }
        }
    }

    suspend fun getMetadata(address: MsgAddressInt): CollectionMetadata? {
        val cachedValue = metadataCache()?.get(address)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {}", kv("address", address.toRaw()))
            cachedValue.get() as? CollectionMetadata
        } else {
            if (approvalRepository.existsByApprovedIsTrueAndAddress(address)) { // Has been explicitly approved
                getContract(address)?.let {
                    CollectionMetadata.of(it.content)
                }

            } else {
                logger.warn("{} was not approved", kv("address", address.toRaw()))
                null
            }
                .also { metadata -> // Cache even when null
                    metadataCache()?.put(address, metadata)
                }
        }
    }

    suspend fun getItemAddress(
        address: MsgAddressInt,
        index: ULong,
        referenceBlock: suspend () -> TonNodeBlockIdExt? = { liteClient.getLastBlockId() }
    ): MsgAddress {
        val key = "${address.toRaw()}:$index"
        val cachedValue = itemAddressCache()?.get(key)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {} {}", kv("address", address.toRaw()), kv("index", index))
            (cachedValue.get() as? MsgAddress) ?: AddrNone
        } else {
            if (approvalRepository.existsByApprovedIsTrueAndAddress(address)) { // Has been explicitly approved
                try {
                    CollectionContract.itemAddressOf(address as AddrStd, index, liteClient, referenceBlock())
                } catch (e: TvmException) {
                    logger.warn(
                        "could not get item {} address of {}",
                        kv("index", index.toString()),
                        kv("collection", address.toRaw())
                    )
                    AddrNone
                }
            } else {
                logger.warn("{} was not approved", kv("address", address.toRaw()))
                AddrNone
            }
                .also { itemAddress ->
                    itemAddressCache()?.put(key, itemAddress)
                }
        }
    }

    suspend fun listItemAddresses(
        address: MsgAddressInt,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getLastBlockId() }
    ): Flow<MsgAddress> {
        return (0uL until (getContract(address, referenceBlock)?.next_item_index ?: 0uL))
            .asFlow()
            .map { getItemAddress(address, it, referenceBlock) }
    }

    private fun contractCache() = cacheManager.getCache("collection.contract")
    private fun metadataCache() = cacheManager.getCache("collection.metadata")
    private fun itemAddressCache() = cacheManager.getCache("collection.item_address")

    companion object : KLogging()
}
