package money.tegro.market.service

import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class ItemService(
    private val cacheManager: CacheManager,
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    suspend fun getContract(address: MsgAddressInt): ItemContract? {
        val cachedValue = contractCache()?.get(address)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {}", kv("address", address.toRaw()))
            cachedValue.get() as? ItemContract
        } else {
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", address.toRaw()))
                null
            } else {
                try {
                    logger.debug("fetching item {}", kv("address", address.toRaw()))
                    ItemContract.of(address as AddrStd, liteClient)
                } catch (e: TvmException) {
                    logger.warn("could not get item information for {}", kv("address", address.toRaw()), e)
                    null
                }
            }
                .also { contract -> // Even if the result is null, cache it
                    contractCache()?.put(address, contract)
                }
        }
    }

    suspend fun getMetadata(address: MsgAddressInt): ItemMetadata? {
        val cachedValue = metadataCache()?.get(address)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {}", kv("address", address.toRaw()))
            cachedValue.get() as? ItemMetadata
        } else {
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", address.toRaw()))
                null
            } else {
                getContract(address)?.let { item ->
                    ItemMetadata.of(
                        (item.collection as? AddrStd) // Collection items
                            ?.let {
                                CollectionContract.itemContent(it, item.index, item.individual_content, liteClient)
                            }
                            ?: item.individual_content) // Standalone items
                        .also { metadata ->
                            metadataCache()?.put(address, metadata)
                        }
                }
            }
                .also { metadata -> // Cache even when null
                    metadataCache()?.put(address, metadata)
                }
        }
    }

    private fun contractCache() = cacheManager.getCache("item.contract")
    private fun metadataCache() = cacheManager.getCache("item.metadata")

    companion object : KLogging()
}
