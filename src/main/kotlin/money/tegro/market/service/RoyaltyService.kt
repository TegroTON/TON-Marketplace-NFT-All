package money.tegro.market.service

import money.tegro.market.accountBlockAddresses
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class RoyaltyService(
    private val cacheManager: CacheManager,
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    suspend fun get(address: MsgAddressInt): RoyaltyContract? {
        val cachedValue = cache()?.get(address)
        return if (cachedValue != null) { // Cache hit
            logger.trace("cache hit on {}", kv("address", address.toRaw()))
            cachedValue.get() as? RoyaltyContract
        } else {
            if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", address.toRaw()))
                null
            } else {
                try {
                    logger.debug("fetching royalty information {}", kv("address", address.toRaw()))
                    RoyaltyContract.of(address as AddrStd, liteClient)
                } catch (e: TvmException) {
                    logger.warn("could not get royalty information for {}", kv("address", address.toRaw()), e)
                    null
                }
            }
                .also { contract -> // Even if the result is null, cache it
                    cache()?.put(address, contract)
                }
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = "blocks.market.royalty",
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
                if (cache()?.evictIfPresent(it) == true)
                    logger.debug("evicted {}", kv("address", it.toRaw()))
            }
    }

    private fun cache() = cacheManager.getCache("royalty")

    companion object : KLogging()
}
